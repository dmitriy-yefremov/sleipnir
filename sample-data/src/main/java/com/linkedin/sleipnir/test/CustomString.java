package com.linkedin.sleipnir.test;

import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.lang.Object;

//
// The custom class
// It has to be immutable.
//
public class CustomString
{
    private String string;

    public CustomString(String s)
    {
        string = s;
    }

    public boolean equals(Object other) {
        if (!(other instanceof CustomString))
            return false;
        CustomString otherAsCustomString = (CustomString) other;
        return toString().equals(otherAsCustomString.toString());
    }

    public String toString()
    {
        return string;
    }
}