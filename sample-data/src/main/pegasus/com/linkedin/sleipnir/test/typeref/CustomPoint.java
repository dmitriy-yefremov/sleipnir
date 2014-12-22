package com.linkedin.sleipnir.test.typeref;

import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DirectCoercer;
import com.linkedin.data.template.TemplateOutputCastException;

import java.lang.Object;

//
// The custom class
// It has to be immutable.
//
public class CustomPoint
{
    private int _x;
    private int _y;

    public CustomPoint(String s)
    {
        String parts[] = s.split(",");
        _x = Integer.parseInt(parts[0]);
        _y = Integer.parseInt(parts[1]);
    }

    public CustomPoint(int x, int y)
    {
        _x = x;
        _y = y;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    // TODO: Implement hashCode, ...
    public boolean equals(Object other) {
        if (!(other instanceof CustomPoint))
            return false;
        CustomPoint otherAsCustomPoint = (CustomPoint) other;
        return toString().equals(other.toString());
    }

    public String toString()
    {
        return _x + "," + _y;
    }

    //
    // The custom class's DirectCoercer.
    //
    public static class CustomPointCoercer implements DirectCoercer<CustomPoint>
    {
        @Override
        public Object coerceInput(CustomPoint object)
                throws ClassCastException
        {
            return object.toString();
        }

        @Override
        public CustomPoint coerceOutput(Object object)
                throws TemplateOutputCastException
        {
            if (object instanceof String == false)
            {
                throw new TemplateOutputCastException("Output " + object +
                        " is not a string, and cannot be coerced to " +
                        CustomPoint.class.getName());
            }
            return new CustomPoint((String) object);
        }
    }

    //
    // Automatically register Java custom class and its coercer.
    //
    static
    {
        Custom.registerCoercer(CustomPoint.class, new CustomPointCoercer());
    }
}