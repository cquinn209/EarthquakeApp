package org.me.gcu.equakestartercode;


//Conor Quinn
//S1705540

public class titleitem {

private String title;
private String description;
private String link;
private String pubDate;
private String categoryy;
private String lat;
private String llong;



public titleitem()
{
    title = "";
    description = "";
    link = "";
    pubDate = "";
    categoryy = "";
    lat = "";
    llong = "";
}


public titleitem(String atitle, String adescription, String alink, String apubDate,
                String acategoryy, String alat, String allong)
{
    title = atitle;
    description = adescription;
    link = alink;
    pubDate = apubDate;
    categoryy = acategoryy;
    lat = alat;
    llong = allong;
}

public String gettitle() { return title; }

public void settitle(String atitle) { title = atitle; }


public String getdescription() { return description;}

public void setdescription(String adescription) { description = adescription;}



public String getlink() {return link;}

public void setlink(String alink) {link = alink;}



public String getpubDate() {return pubDate;}

public void setpubDate(String apubDate) {pubDate = apubDate;}



public String getcategoryy() {return categoryy;}

public void setcategoryy(String acategoryy) {categoryy = acategoryy;}



public String getlat() {return lat;}

public void setlat(String alat) {lat = alat;}


public String getllong() {return llong;}

public void setllong(String allong) {llong = allong;}





public String toString()
{
    String temp;

    temp = title +description + link + pubDate + categoryy + lat + llong;

    return temp;



}


 }

