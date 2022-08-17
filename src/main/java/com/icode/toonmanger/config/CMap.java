package com.icode.toonmanger.config;

import java.util.*;

public class CMap implements Map<String,Object>{
    Map<String,Object> map;


    public CMap(Map<String,Object> baseMap) {
        this.map = baseMap;
    }


    public CMap(){
        this(new HashMap<String,Object>());
    }



    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {

        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key,value);
    }


    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public String getS(String key){
        return getS(key,"");
    }
    public String getS(String key,String defaultValue){
        return this.map.getOrDefault(key,defaultValue).toString();
    }

    public boolean eqS(String key,String v){ String a=getS(key); if(a==null || !a.equals(v)){ return false;} return true;}

    public int getI(String key, int i){
        try {
            return Integer.parseInt(this.getS(key), 10);
        }catch(Exception e){
            return i;
        }
    }

    public Object getOne(String a){
        Object rr = this.get(a);
        if( rr instanceof List){
            List aa = (List) rr;
            return aa.get(0);
        }else{
            return rr;
        }
    }

    public double getD(String key, double i){
        return Double.parseDouble(this.getS(key));
    }

    public long getL(String key){
        return Long.parseLong(this.getS(key),10);
    }

    public List<CMap> getList(String key) { try{return (List<CMap>)this.get(key);}catch(Exception e){return new ArrayList<CMap>();}}

    @Override
    public String toString() {
        return super.toString();
    }
}
