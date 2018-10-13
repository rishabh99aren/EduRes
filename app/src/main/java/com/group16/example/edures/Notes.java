package com.group16.example.edures;

public class Notes {
    private String Branch;
    private String Course;
    private String Download_url;
    //private String Email;
    private int Sem;
    private int Year;
    private String Title;

    public String getBranch() {
        return Branch;
    }

    public String getCourse() {
        return Course;
    }

    public String getDownload_url() {
        return Download_url;
    }

    //public String getEmail(){ return Email;}

    public int getSem() {
        return Sem;
    }

    public int getYear() {
        return Year;
    }

    public String getTitle() {
        return Title;
    }

    public Notes()
    {

    }

    public Notes(String branch,String co,String url, int sem,int year,String title)
    {
        this.Branch=branch;
        this.Course=co;
        this.Download_url=url;
        //this.Email=email;
        this.Sem=sem;
        this.Year=year;
        this.Title=title;
    }

}
