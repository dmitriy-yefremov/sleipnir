package com.linkedin.sleipnir.test;

import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

//
// The custom class's DirectCoercer.
//
public class CustomStringCoercer implements DirectCoercer<CustomString>
{

    //Auto-register this coercer.  See {@link Custom#initializeCoercerClass}
    private static final boolean REGISTER_COERCER =
            Custom.registerCoercer(new CustomStringCoercer(), CustomString.class);

    @Override
    public Object coerceInput(CustomString object)
            throws ClassCastException
    {
        return object.toString();
    }

    @Override
    public CustomString coerceOutput(Object object)
            throws TemplateOutputCastException
    {
        if (object instanceof String == false)
        {
            throw new TemplateOutputCastException("Output " + object +
                    " is not a string, and cannot be coerced to " +
                    CustomString.class.getName());
        }
        return new CustomString((String) object);
    }
}

