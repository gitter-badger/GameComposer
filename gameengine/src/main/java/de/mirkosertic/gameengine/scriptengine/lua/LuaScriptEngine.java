package de.mirkosertic.gameengine.scriptengine.lua;

import de.mirkosertic.gameengine.scriptengine.ScriptEngine;
import de.mirkosertic.gameengine.type.ClassInformation;
import de.mirkosertic.gameengine.type.Field;
import de.mirkosertic.gameengine.type.Method;
import de.mirkosertic.gameengine.type.Reflectable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaScriptEngine implements ScriptEngine {

    private static LuaValue toLuaValue(Object aValue) {
        if (aValue == null) {
            return LuaValue.NIL;
        }
        if (aValue instanceof Reflectable) {
            return toLuaValue((Reflectable) aValue);
        }
        if (aValue instanceof Double) {
            return LuaDouble.valueOf((double) aValue);
        }
        if (aValue instanceof Float) {
            return LuaDouble.valueOf((float) aValue);
        }
        if (aValue instanceof Integer) {
            return LuaInteger.valueOf((int) aValue);
        }
        if (aValue instanceof Long) {
            return LuaInteger.valueOf((long) aValue);
        }
        if (aValue instanceof String) {
            return LuaString.valueOf((String) aValue);
        }
        throw new IllegalArgumentException("Unsupported datatype for " + aValue);
    }

    private static LuaValue toLuaValue(Reflectable aObject) {
        LuaTable theTable = new LuaTable();
        ClassInformation theClassInformation = aObject.getClassInformation();
        for (Field theField : theClassInformation.getFields()) {
            theTable.set(theField.getName(), new FieldAccessFunction(aObject, theField));
            // TODO: add field write methods to table
        }
        for (Method theMethod : theClassInformation.getMethods()) {
            theTable.set(theMethod.getName(), new MethodInvocationFunction(aObject, theMethod));
        }
        return theTable;
    }

    private static Object toJavaValue(LuaValue aValue, Class aTargetClass) {
        if (aTargetClass == String.class) {
            return aValue.toString();
        }
        if (aTargetClass == Integer.class) {
            return aValue.toint();
        }
        if (aTargetClass == Long.class) {
            return aValue.tolong();
        }
        if (aTargetClass == Float.class) {
            return aValue.tofloat();
        }
        if (aTargetClass == Double.class) {
            return aValue.todouble();
        }
        throw new IllegalArgumentException("Cannot convert " + aValue+" to " + aTargetClass);
    }

    private static Object toJavaValue(LuaValue aValue) {
        if (aValue.isnil()) {
            return null;
        }
        if (aValue.islong()) {
            return aValue.tolong();
        }
        return aValue.toString();
    }

    private static class FieldAccessFunction extends ZeroArgFunction {

        private final Object object;
        private final Field field;

        public FieldAccessFunction(Object aObject, Field aField) {
            object = aObject;
            field = aField;
        }

        @Override
        public LuaValue call() {
            return toLuaValue(field.getValue(object));
        }
    }

    private static class MethodInvocationFunction extends VarArgFunction {

        private final Object object;
        private final Method method;

        public MethodInvocationFunction(Object aObject, Method aMethod) {
            object = aObject;
            method = aMethod;
        }

        @Override
        public Varargs invoke(Varargs aArguments) {
            Object[] theArguments;
            if (aArguments instanceof LuaValue) {
                // Single value provided
                if (method.getArgument().length == 1) {
                    theArguments = new Object[1];
                    theArguments[0] = toJavaValue((LuaValue) aArguments, method.getArgument()[0]);
                } else {
                    throw new IllegalArgumentException(method.getArgument().length+" arguments required for " + method.getName());
                }
            } else {
                theArguments = new Object[aArguments.narg()];
                if (aArguments.narg() == method.getArgument().length) {
                    for (int i = 0; i < aArguments.narg(); i++) {
                        theArguments[i] = toJavaValue(aArguments.arg(i));
                    }
                } else {
                    throw new IllegalArgumentException(method.getArgument().length+" arguments required for " + method.getName());
                }
            }
            return toLuaValue(method.invoke(object, theArguments));
        }
    }

    private final Globals globals;
    private final LuaClosure closure;
    private final LuaValue proceedGameFunction;

    public LuaScriptEngine(Globals aGlobals, LuaClosure aClosure) {
        globals = aGlobals;
        closure = aClosure;

        // Initialize the code
        closure.call();

        // Retrieve function from globals
        proceedGameFunction = globals.get("proceedGame");
        if (proceedGameFunction == null) {
            throw new IllegalStateException("No proceedGame function defined");
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void registerObject(String aObjectName, Reflectable aObject) {
        globals.set(aObjectName, toLuaValue(aObject));
    }

    @Override
    public void registerPrimitive(String aObjectName, long aValue) {
        globals.set(aObjectName, LuaInteger.valueOf(aValue));
    }

    @Override
    public Object proceedGame(long aGameTime, long aElapsedTimeSinceLastLoop) {

        Varargs theArguments = LuaValue.varargsOf(new LuaValue[] {
            LuaInteger.valueOf(aGameTime),
            LuaInteger.valueOf(aElapsedTimeSinceLastLoop)
        });

        Varargs theResult = proceedGameFunction.invoke(theArguments);
        if (theResult instanceof LuaValue) {
            return toJavaValue((LuaValue) theResult);
        }
        throw new IllegalStateException("Not supported return type : " + theResult);
    }
}