package com.group16.example.edures;

public class Questions {
    private String Branch;
    private String Course;
    private String Email;
    private String Sem;
    private String Type;

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

    public String getType() { return Type; }

    public Questions()
    {

    }

    public Questions(String branch,String co, String email, String sem, String type) {
        this.Branch=branch;
        this.Course=co;
        this.Email=email;
        this.Sem=sem;
        this.Type=type;
    }
}
