package com.ConsumptionCalculator;

import java.io.Serializable;
import java.util.Locale;

public class UserSettings implements Serializable { //Object to save UserSettings
    private boolean emphasizeInputErrors;
    private boolean highlightInputErrors;
    private boolean describeInputErrors;
    private boolean highlightDescribeInputErrors;
    private boolean doAutoInputCheck;
    private Locale programLocale; //saves Program Local
    private boolean useSystemLocale;

    public UserSettings(){
        setEmphasizeInputErrors(true);
        setHighlightInputErrors(true);
        setDescribeInputErrors(true);
        setHighlightDescribeInputErrors(true);
        setDoAutoInputCheck(true);
        setProgramLocale(Locale.getDefault()); //sets System Local as programLocal
        setUseSystemLocale(true);
    }

    public UserSettings(boolean emphasizeInputErrors, boolean highlightInputErrors, boolean describeInputErrors, boolean highlightDescribeInputErrors, boolean doAutoInputCheck, Locale programLocale, boolean useSystemLocale){
        setEmphasizeInputErrors(emphasizeInputErrors);
        setHighlightInputErrors(highlightInputErrors);
        setDescribeInputErrors(describeInputErrors);
        setHighlightDescribeInputErrors(highlightDescribeInputErrors);
        setDoAutoInputCheck(doAutoInputCheck);
        setProgramLocale(programLocale); //sets System Local as programLocal
        setUseSystemLocale(useSystemLocale);
    }

    public boolean getEmphasizeInputErrors(){
        return this.emphasizeInputErrors;
    }

    public boolean getHighlightInputErrors(){
        return this.highlightInputErrors;
    }

    public boolean getDescribeInputErrors(){
        return this.describeInputErrors;
    }

    public boolean getHighlightDescribeInputErrors(){
        return this.highlightDescribeInputErrors;
    }

    public boolean getDoAutoInputCheck() {return this.doAutoInputCheck; }

    public Locale getProgramLocale() {return this.programLocale; }

    public boolean getUseSystemLocale() {return  this.useSystemLocale; }

    public void setEmphasizeInputErrors(boolean value ){
        this.emphasizeInputErrors = value;
    }

    public void setHighlightInputErrors(boolean value ){
        this.highlightInputErrors = value;
    }

    public void setDescribeInputErrors(boolean value ){
        this.describeInputErrors = value;
    }

    public void setHighlightDescribeInputErrors(boolean value ){
        this.highlightDescribeInputErrors = value;
    }

    public void setDoAutoInputCheck(boolean value ){
        this.doAutoInputCheck = value;
    }

    public void setProgramLocale(Locale value ) {
         this.programLocale = value;
    }

    public void setUseSystemLocale(boolean value) {
        this.useSystemLocale = value;
    }
}
