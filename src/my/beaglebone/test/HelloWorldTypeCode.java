
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package my.beaglebone.test;

import com.rti.dds.typecode.*;

public class  HelloWorldTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[1];
        Annotations memberAnnotation;

        memberAnnotation = new Annotations();
        memberAnnotation.default_annotation(AnnotationParameterValue.EMPTY_STRING);
        sm[__i] = new  StructMember("str", false, (short)-1,  false, new TypeCode(TCKind.TK_STRING,255), 0, false, memberAnnotation);__i++;

        Annotations annotation = new Annotations();
        annotation.allowed_data_representation_mask(5);

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("HelloWorld",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm , annotation);        
        return tc;
    }
}

