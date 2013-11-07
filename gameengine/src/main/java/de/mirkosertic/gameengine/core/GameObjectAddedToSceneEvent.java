package de.mirkosertic.gameengine.core;

public class GameObjectAddedToSceneEvent extends GameEvent {

    private GameObject object;

    public GameObjectAddedToSceneEvent(GameObject aObject) {
        object = aObject;
    }

    public GameObject getGameObjectInstance() {
        return object;
    }
}