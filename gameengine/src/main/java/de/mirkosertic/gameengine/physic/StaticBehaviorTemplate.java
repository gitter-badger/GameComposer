package de.mirkosertic.gameengine.physic;

import de.mirkosertic.gameengine.core.BehaviorTemplate;
import de.mirkosertic.gameengine.core.GameObject;
import de.mirkosertic.gameengine.core.GameObjectInstance;
import de.mirkosertic.gameengine.core.GameRuntime;
import de.mirkosertic.gameengine.core.UsedByReflection;
import de.mirkosertic.gameengine.event.GameEventManager;
import de.mirkosertic.gameengine.type.Reflectable;

import java.util.HashMap;
import java.util.Map;

public class StaticBehaviorTemplate implements BehaviorTemplate<StaticBehavior>, Static, Reflectable<StaticClassInformation> {

    private static final StaticClassInformation CIINSTANCE = new StaticClassInformation();

    private final GameObject owner;

    @UsedByReflection
    public StaticBehaviorTemplate(GameEventManager aEventManager, GameObject aOwner) {
        owner = aOwner;
    }

    @Override
    public StaticClassInformation getClassInformation() {
        return CIINSTANCE;
    }

    @Override
    public GameObject getOwner() {
        return owner;
    }

    @Override
    public StaticBehavior create(GameObjectInstance aInstance, GameRuntime aGameRuntime) {
        return new StaticBehavior(aInstance);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> theResult = new HashMap<>();
        theResult.put(StaticBehavior.TYPE_ATTRIBUTE, StaticBehavior.TYPE);
        return theResult;
    }

    @Override
    public void delete() {
        owner.getGameScene().removeBehaviorFrom(owner, this);
    }

    public static StaticBehaviorTemplate deserialize(GameEventManager aEventManager, GameObject aOwner) {
        return new StaticBehaviorTemplate(aEventManager, aOwner);
    }
}