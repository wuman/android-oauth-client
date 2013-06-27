
package com.wuman.oauth.samples.github.api;

import com.google.api.client.util.ArrayValueMap;
import com.google.api.client.util.ClassInfo;
import com.google.api.client.util.Data;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Lists;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Throwables;
import com.google.api.client.util.Types;
import com.google.api.client.util.escape.CharEscapers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LinkHeaderParser implements ObjectParser {

    public static void parse(String content, Object data) {
        if (content == null) {
            return;
        }
        try {
            parse(new StringReader(content), data);
        } catch (IOException exception) {
            // I/O exception not expected on a string
            throw Throwables.propagate(exception);
        }
    }

    public static void parse(Reader reader, Object data) throws IOException {
        Class<?> clazz = data.getClass();
        ClassInfo classInfo = ClassInfo.of(clazz);
        List<Type> context = Arrays.<Type> asList(clazz);
        GenericData genericData =
                GenericData.class.isAssignableFrom(clazz) ? (GenericData) data : null;
        @SuppressWarnings("unchecked")
        Map<Object, Object> map =
                Map.class.isAssignableFrom(clazz) ? (Map<Object, Object>) data : null;
        ArrayValueMap arrayValueMap = new ArrayValueMap(data);
        StringWriter nameWriter = new StringWriter();
        StringWriter valueWriter = new StringWriter();
        boolean readingValue = true;
        boolean readingName = false;
        mainLoop: while (true) {
            int read = reader.read();
            switch (read) {
                case -1:
                    // falls through
                case ',':
                    // parse name/value pair
                    String name = CharEscapers.decodeUri(nameWriter.toString());
                    if (name.length() != 0) {
                        String stringValue = CharEscapers.decodeUri(valueWriter.toString());
                        // get the field from the type information
                        FieldInfo fieldInfo = classInfo.getFieldInfo(name);
                        if (fieldInfo != null) {
                            Type type =
                                    Data.resolveWildcardTypeOrTypeVariable(context,
                                            fieldInfo.getGenericType());
                            // type is now class, parameterized type, or generic
                            // array type
                            if (Types.isArray(type)) {
                                // array that can handle repeating values
                                Class<?> rawArrayComponentType =
                                        Types.getRawArrayComponentType(context,
                                                Types.getArrayComponentType(type));
                                arrayValueMap.put(fieldInfo.getField(), rawArrayComponentType,
                                        parseValue(rawArrayComponentType, context, stringValue));
                            } else if (Types.isAssignableToOrFrom(
                                    Types.getRawArrayComponentType(context, type), Iterable.class)) {
                                // iterable that can handle repeating values
                                @SuppressWarnings("unchecked")
                                Collection<Object> collection = (Collection<Object>) fieldInfo
                                        .getValue(data);
                                if (collection == null) {
                                    collection = Data.newCollectionInstance(type);
                                    fieldInfo.setValue(data, collection);
                                }
                                Type subFieldType = type == Object.class ? null : Types
                                        .getIterableParameter(type);
                                collection.add(parseValue(subFieldType, context, stringValue));
                            } else {
                                // parse into a field that assumes it is a
                                // single value
                                fieldInfo.setValue(data, parseValue(type, context, stringValue));
                            }
                        } else if (map != null) {
                            // parse into a map: store as an ArrayList of values
                            @SuppressWarnings("unchecked")
                            ArrayList<String> listValue = (ArrayList<String>) map.get(name);
                            if (listValue == null) {
                                listValue = Lists.<String> newArrayList();
                                if (genericData != null) {
                                    genericData.set(name, listValue);
                                } else {
                                    map.put(name, listValue);
                                }
                            }
                            listValue.add(stringValue);
                        }
                    }
                    // ready to read next name/value pair
                    readingValue = true;
                    readingName = false;
                    nameWriter = new StringWriter();
                    valueWriter = new StringWriter();
                    if (read == -1) {
                        break mainLoop;
                    }
                    break;
                case ';':
                    // finished with value, now read name
                    readingValue = false;
                    break;
                case '<':
                case '>':
                case ' ':
                case '"':
                    // skip
                    break;
                case '=':
                    if (!readingValue) {
                        readingName = true;
                        break;
                    }
                default:
                    // read one more character
                    if (readingValue) {
                        valueWriter.write(read);
                    } else {
                        if (readingName) {
                            nameWriter.write(read);
                        }
                    }
            }
        }
        arrayValueMap.setValues();
    }

    private static Object parseValue(Type valueType, List<Type> context, String value) {
        Type resolved = Data.resolveWildcardTypeOrTypeVariable(context, valueType);
        return Data.parsePrimitiveValue(resolved, value);
    }

    @Override
    public <T> T parseAndClose(InputStream in, Charset charset, Class<T> dataClass)
            throws IOException {
        InputStreamReader r = new InputStreamReader(in, charset);
        return parseAndClose(r, dataClass);
    }

    @Override
    public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
        InputStreamReader r = new InputStreamReader(in, charset);
        return parseAndClose(r, dataType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseAndClose(Reader reader, Class<T> dataClass) throws IOException {
        return (T) parseAndClose(reader, (Type) dataClass);
    }

    @Override
    public Object parseAndClose(Reader reader, Type dataType) throws IOException {
        Preconditions.checkArgument(
                dataType instanceof Class<?>, "dataType has to be of type Class<?>");

        Object newInstance = Types.newInstance((Class<?>) dataType);
        parse(new BufferedReader(reader), newInstance);
        return newInstance;
    }

}
