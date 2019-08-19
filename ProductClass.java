package vanunu.deeznuts;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by Nevo Vanunu on 30/10/2017.
 */
public class ProductClass {
    public String name,price,description,imgurl,owner,catagory,country,groupid;
    public int id;
    public boolean status;//True=product is on sale ; False=product is deleted/bought.

    public ProductClass()
    {

    }

    public ProductClass(String name, String price, String description,int id,String url,String owner,String catagory,String country,String gid) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.id=id;
        this.imgurl=url;
        this.status=true;
        this.owner=owner;
        this.catagory=catagory;
        this.country=country;
        this.groupid=gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgurl() {
        return imgurl;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getCountry() {
        return country;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
