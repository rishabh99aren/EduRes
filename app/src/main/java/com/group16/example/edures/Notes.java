package com.group16.example.edures;

public class Notes {
    private String Branch;
    private String Course;
    private String Email;
    private String Sem;

    public String getBranch() {
        return Branch;
    }

    public String getCourse() {
        return Course;
    }

    public String getEmail(){ return Email;}

    public String getSem() {
        return Sem;
    }

    public Notes()
    {

    }

    public Notes(String branch,String co, String email, String sem)
    {
        this.Branch=branch;
        this.Course=co;
        this.Email=email;
        this.Sem=sem;
    }

}
