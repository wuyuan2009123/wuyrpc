package com.wuy.rpc.netty.media;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.wuy.rpc.netty.handler.param.ServerRequest;
import com.wuy.rpc.netty.rpcfilter.RpcFilter;
import com.wuy.rpc.pojo.Response;

public class Media {
	public static Map<String,BeanMethod> beanMap;
	public static TreeMap<Integer,RpcFilter> filterMap;
    static{
        beanMap= new HashMap<String,BeanMethod>();
        filterMap=new TreeMap<Integer,RpcFilter>();
    }
     
    private static Media m=null;
    private Media(){
         
    }
     
     
    public static Media newInstance() {
        if(m==null){
            m=new Media();
        }
        return m;
    }
    
    //反射处理业务代码
    public Response process(ServerRequest request) {
        Response result=null;
        try {
            String command = request.getCommand();
            BeanMethod beanMethod = beanMap.get(command);
            if(beanMethod==null){
                return null;
            }
            Object bean = beanMethod.getBean();
            Method m = beanMethod.getMethod();
            Class<?>[] parameterTypes = m.getParameterTypes();
            Object[] parameters = request.getParameters();
            Object[] params=new Object[parameterTypes.length];
            if(null!=parameterTypes){
            	for(int i=0;i<parameterTypes.length;i++){
            		Object param = JSONObject.parseObject(JSONObject.toJSONString(parameters[i]), parameterTypes[i]);
            		params[i]=param;
                }
            }
            boolean flagfiter=true;
            List<RpcFilter> filterList = getFilterList();
            if(null!=filterList&&filterList.size()>0){
            	 for(RpcFilter filter:filterList){
                 	boolean filterBean = filter.doBeforeRequest(m, bean, params);
                 	if(!filterBean){
                 		flagfiter=false;
                 	}
                 }
                 if(flagfiter){
                 	result = (Response) m.invoke(bean, params);
                 }
                 else{
					throw new RuntimeException("无效的请求，服务端已经拒绝回应");
				 }
                 for(RpcFilter filter:filterList){
                	 filter.doAfterRequest(result);
                 }
            }else{
            	result = (Response) m.invoke(bean, params);
            }
            
            result.setId(request.getId());
        }catch (Exception e) {
            e.printStackTrace();
        }
         
        return result;
    }


	private List<RpcFilter> getFilterList() {
		List<RpcFilter> filterList=new ArrayList<RpcFilter>();
		if(!CollectionUtils.isEmpty(filterMap)){
			Iterator<Entry<Integer, RpcFilter>> iterator = filterMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Integer, RpcFilter> next = iterator.next();
				filterList.add(next.getValue());
			}
		}
		return filterList;
	}
}
