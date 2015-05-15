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