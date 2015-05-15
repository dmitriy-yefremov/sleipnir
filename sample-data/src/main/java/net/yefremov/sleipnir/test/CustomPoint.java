/*
 *    Copyright 2015 Dmitriy Yefremov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.yefremov.sleipnir.test;

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
    private int x;
    private int y;

    public CustomPoint(String s)
    {
        String parts[] = s.split(",");
        x = Integer.parseInt(parts[0]);
        y = Integer.parseInt(parts[1]);
    }

    public CustomPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean equals(Object other) {
        if (!(other instanceof CustomPoint))
            return false;
        CustomPoint otherAsCustomPoint = (CustomPoint) other;
        return toString().equals(otherAsCustomPoint.toString());
    }

    public String toString()
    {
        return x + "," + y;
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
            if (!(object instanceof String))
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
        Custom.registerCoercer(new CustomPointCoercer(), CustomPoint.class);
    }
}