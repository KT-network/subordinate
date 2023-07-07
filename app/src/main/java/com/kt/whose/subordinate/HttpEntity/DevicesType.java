
package com.kt.whose.subordinate.HttpEntity;
import java.io.Serializable;
import java.util.Date;

public class DevicesType implements Serializable {

    private int id;
    private String name;
    private String type;
    private String picUrl;
    private String size;
    private String createTime;
    public void setId(int id) {
         this.id = id;
     }
     public int getId() {
         return id;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setPicUrl(String picUrl) {
         this.picUrl = picUrl;
     }
     public String getPicUrl() {
         return picUrl;
     }

    public void setSize(String size) {
         this.size = size;
     }
     public String getSize() {
         return size;
     }

    public void setCreateTime(String createTime) {
         this.createTime = createTime;
     }
     public String getCreateTime() {
         return createTime;
     }

}