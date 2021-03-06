package de.mirkosertic.gamecomposer;

import de.mirkosertic.gameengine.AbstractGameRuntimeFactory;
import de.mirkosertic.gameengine.javafx.JDKBuiltInFunctions;
import de.mirkosertic.gameengine.physics.jbox2d.JBox2DGamePhysicsManagerFactory;
import de.mirkosertic.gameengine.scriptengine.luaj.LuaJScriptEngineFactory;

import javax.inject.Singleton;

@Singleton
public class PreviewGameRuntimeFactory extends AbstractGameRuntimeFactory {

    @Override
    protected LuaJScriptEngineFactory createScriptEngine() {
        return new LuaJScriptEngineFactory(new JDKBuiltInFunctions());
    }

    @Override
    protected JBox2DGamePhysicsManagerFactory createPhysicsManagerFactory() {
        return new JBox2DGamePhysicsManagerFactory();
    }
}