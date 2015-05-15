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
        if (!(object instanceof String))
        {
            throw new TemplateOutputCastException("Output " + object +
                    " is not a string, and cannot be coerced to " +
                    CustomString.class.getName());
        }
        return new CustomString((String) object);
    }
}

