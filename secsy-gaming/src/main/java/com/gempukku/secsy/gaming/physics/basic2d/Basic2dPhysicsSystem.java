package com.gempukku.secsy.gaming.physics.basic2d;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.*;
import com.gempukku.secsy.entity.event.Event;
import com.gempukku.secsy.entity.index.EntityIndex;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.gaming.component.HorizontalOrientationComponent;
import com.gempukku.secsy.gaming.component.Position2DComponent;
import com.gempukku.secsy.gaming.component.PositionResolver;
import com.gempukku.secsy.gaming.component.Size2DComponent;
import com.gempukku.secsy.gaming.physics.PhysicsSystem;
import com.gempukku.secsy.gaming.time.TimeManager;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

@RegisterSystem(profiles = "basic2dPhysics", shared = {PhysicsSystem.class, Basic2dPhysics.class})
public class Basic2dPhysicsSystem extends AbstractLifeCycleSystem implements PhysicsSystem, Basic2dPhysics, EntityListener {
    // Maximum at 60fps
    private static final float DEFAULT_MAX_TIME_STEP = 1 / 60f;
    private static final int DEFAULT_GRAVITY = -10;
    // Terminal velocity of a human in air
    private static final float DEFAULT_TERMINAL_VELOCITY = 53;

    @Inject
    private InternalEntityManager internalEntityManager;
    @Inject
    private EntityIndexManager entityIndexManager;
    @Inject
    private TimeManager timeManager;
    @Inject(optional = true)
    private CollisionFilter collisionFilter;
    @Inject(optional = true)
    private GravityProvider gravityProvider;
    @Inject(optional = true)
    private TimeStepProvider timeStepProvider;

    private Map<Integer, CollidingBody> collidingBodies = new HashMap<Integer, CollidingBody>();
    private Map<Integer, Obstacle> obstacles = new HashMap<Integer, Obstacle>();
    private Map<Integer, Map<String, Sensor>> sensors = new HashMap<Integer, Map<String, Sensor>>();
    private Map<Integer, SensorTrigger> sensorTriggers = new HashMap<Integer, SensorTrigger>();

    private Multimap<Sensor, SensorTrigger> existingSensorContacts = HashMultimap.create();

    private EntityIndex movingEntities;
    private EntityIndex affectedByGravity;

    @Override
    public void initialize() {
        internalEntityManager.addEntityListener(this);
        movingEntities = entityIndexManager.addIndexOnComponents(MovingComponent.class);
        affectedByGravity = entityIndexManager.addIndexOnComponents(AffectedByGravityComponent.class);
    }

    @Override
    public Iterable<EntityRef> getSensorTriggersFor(EntityRef sensorEntity, String sensorType,
                                                    float minX, float maxX, float minY, float maxY,
                                                    Predicate<EntityRef> sensorTriggerPredicate) {
        List<EntityRef> result = new LinkedList<EntityRef>();
        for (SensorTrigger sensorTrigger : sensorTriggers.values()) {
            if (collisionFilter != null) {
                EntityRef sensorTriggerEntity = internalEntityManager.getEntityById(sensorTrigger.entityId);
                if (collisionFilter.canSensorContact(sensorType, sensorEntity, sensorTriggerEntity)
                        && hasContact(minX, maxX, minY, maxY, sensorTrigger)
                        && sensorTriggerPredicate.apply(sensorTriggerEntity)) {
                    result.add(sensorTriggerEntity);
                }
            } else {
                if (hasContact(minX, maxX, minY, maxY, sensorTrigger)) {
                    EntityRef sensorTriggerEntity = internalEntityManager.getEntityById(sensorTrigger.entityId);
                    if (sensorTriggerPredicate.apply(sensorTriggerEntity)) {
                        result.add(sensorTriggerEntity);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Iterable<EntityRef> getContactsForSensor(EntityRef sensorEntity, String type, Predicate<EntityRef> sensorTriggerPredicate) {
        List<EntityRef> results = new LinkedList<EntityRef>();
        int entityId = internalEntityManager.getEntityId(sensorEntity);
        for (Map.Entry<Sensor, SensorTrigger> contact : existingSensorContacts.entries()) {
            if (contact.getKey().entityId == entityId && contact.getKey().type.equals(type)) {
                EntityRef sensorTriggerEntity = internalEntityManager.getEntityById(contact.getValue().entityId);
                if (sensorTriggerPredicate.apply(sensorTriggerEntity))
                    results.add(sensorTriggerEntity);
            }
        }

        return results;
    }

    @Override
    public Iterable<EntityRef> getSensorEntitiesContactedBy(EntityRef sensorTriggerEntity, String sensorType) {
        List<EntityRef> results = new LinkedList<EntityRef>();
        int entityId = internalEntityManager.getEntityId(sensorTriggerEntity);
        for (Map.Entry<Sensor, SensorTrigger> contact : existingSensorContacts.entries()) {
            if (contact.getValue().entityId == entityId && contact.getKey().type.equals(sensorType)) {
                EntityRef sensorEntity = internalEntityManager.getEntityById(contact.getKey().entityId);
                results.add(sensorEntity);
            }
        }

        return results;
    }

    @Override
    public void processPhysics() {
        float seconds = timeManager.getTimeSinceLastUpdate() / 1000f;

        float maxTimeStep = getMaxTimeStep();
        while (seconds > 0) {
            float iterationTime = Math.min(seconds, maxTimeStep);
            applyGravity(iterationTime);
            applyMovement(iterationTime);
            processCollisions();
            updatePositions();
            processSensors();
            seconds -= iterationTime;
        }
    }

    private float getMaxTimeStep() {
        if (timeStepProvider != null)
            return timeStepProvider.getMaxTimeStep();
        return DEFAULT_MAX_TIME_STEP;
    }

    private void applyGravity(float seconds) {
        for (EntityRef entity : affectedByGravity) {
            float gravity = getGravity(entity);
            float terminalVelocity = getTerminalVelocity(entity);

            MovingComponent movingComponent = entity.getComponent(MovingComponent.class);
            float desiredSpeed = movingComponent.getSpeedY() + gravity * seconds;
            float speedY = Math.signum(desiredSpeed) * Math.min(terminalVelocity, Math.abs(desiredSpeed));
            movingComponent.setSpeedY(speedY);
            entity.saveChanges();
        }
    }

    private float getGravity(EntityRef entity) {
        if (gravityProvider != null)
            return gravityProvider.getGravityForEntity(entity);
        return DEFAULT_GRAVITY;
    }

    private float getTerminalVelocity(EntityRef entity) {
        if (gravityProvider != null)
            return gravityProvider.getTerminalVelocityForEntity(entity);
        return DEFAULT_TERMINAL_VELOCITY;
    }

    private void processSensors() {
        Multimap<Sensor, SensorTrigger> newContacts = HashMultimap.create();

        Multimap<EntityRef, Event> eventsToSend = HashMultimap.create();

        for (Map.Entry<Integer, Map<String, Sensor>> sensorMap : sensors.entrySet()) {
            int entityId = sensorMap.getKey();
            EntityRef sensorEntity = internalEntityManager.getEntityById(entityId);
            Position2DComponent position = sensorEntity.getComponent(Position2DComponent.class);
            float x = position.getX();
            float y = position.getY();
            for (Sensor sensor : sensorMap.getValue().values()) {
                for (SensorTrigger sensorTrigger : sensorTriggers.values()) {
                    if (sensor.entityId != sensorTrigger.entityId) {
                        if (collisionFilter != null) {
                            EntityRef sensorTriggerEntity = internalEntityManager.getEntityById(sensorTrigger.entityId);
                            if (collisionFilter.canSensorContact(sensor.type, sensorEntity, sensorTriggerEntity)
                                    && hasContact(sensor, x, y, sensorTrigger)) {
                                if (!existingSensorContacts.containsEntry(sensor, sensorTrigger))
                                    eventsToSend.put(sensorEntity, new SensorContactBegin(sensor.type, sensorTriggerEntity));
                                newContacts.put(sensor, sensorTrigger);
                            }
                        } else {
                            if (hasContact(sensor, x, y, sensorTrigger)) {
                                if (!existingSensorContacts.containsEntry(sensor, sensorTrigger)) {
                                    EntityRef sensorTriggerEntity = internalEntityManager.getEntityById(sensorTrigger.entityId);
                                    eventsToSend.put(sensorEntity, new SensorContactBegin(sensor.type, sensorTriggerEntity));
                                }
                                newContacts.put(sensor, sensorTrigger);
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<Sensor, SensorTrigger> contactEntry : existingSensorContacts.entries()) {
            Sensor sensor = contactEntry.getKey();
            SensorTrigger sensorTrigger = contactEntry.getValue();
            if (!newContacts.containsEntry(sensor, sensorTrigger)) {
                EntityRef sensorEntity = internalEntityManager.getEntityById(sensor.entityId);
                if (sensorEntity != null) {
                    EntityRef sensorTriggerEntity = internalEntityManager.getEntityById(sensorTrigger.entityId);
                    eventsToSend.put(sensorEntity, new SensorContactEnd(sensor.type, sensorTriggerEntity));
                }
            }
        }

        existingSensorContacts = newContacts;

        for (Map.Entry<EntityRef, Event> eventEntry : eventsToSend.entries()) {
            eventEntry.getKey().send(eventEntry.getValue());
        }
    }

    private boolean hasContact(Sensor sensor, float x, float y, SensorTrigger sensorTrigger) {
        return hasContact(x + sensor.left, x + sensor.right, y + sensor.down, y + sensor.up, sensorTrigger);
    }

    private boolean hasContact(float x1Min, float x1Max, float y1Min, float y1Max, SensorTrigger sensorTrigger) {
        float x2Min = sensorTrigger.x + sensorTrigger.left;
        float x2Max = sensorTrigger.x + sensorTrigger.right;
        float y2Min = sensorTrigger.y + sensorTrigger.down;
        float y2Max = sensorTrigger.y + sensorTrigger.up;

        if (x1Max > x2Min && x1Min < x2Max
                && y1Max > y2Min && y1Min < y2Max) {
            // There could be an overlap
            if (sensorTrigger.isAABB) {
                return true;
            } else {
                // Check for not AABBs
                // Using the Separating Axis Test
                return SeparatingAxisTest.findOverlap(x1Min, x1Max, y1Min, y1Max,
                        sensorTrigger.nonAABBVertices, new Vector2()) != null;
            }
        } else {
            return false;
        }
    }

    private void applyMovement(float seconds) {
        for (EntityRef movingEntity : movingEntities) {
            int entityId = internalEntityManager.getEntityId(movingEntity);
            if (collidingBodies.containsKey(entityId) || obstacles.containsKey(entityId)) {
                MovingComponent moving = movingEntity.getComponent(MovingComponent.class);
                Position2DComponent position = movingEntity.getComponent(Position2DComponent.class);
                float oldX = position.getX();
                float oldY = position.getY();
                float newX = oldX + moving.getSpeedX() * seconds;
                float newY = oldY + moving.getSpeedY() * seconds;

                CollidingBody collidingBody = collidingBodies.get(entityId);
                if (collidingBody != null)
                    collidingBody.updatePositions(oldX, oldY, newX, newY);
                Obstacle obstacle = obstacles.get(entityId);
                if (obstacle != null)
                    obstacle.updatePositions(oldX, oldY, newX, newY);
            }
        }
    }

    private void updatePositions() {
        for (EntityRef movingEntity : movingEntities) {
            int entityId = internalEntityManager.getEntityId(movingEntity);
            // If an entity is both a CollidingBody and an Obstacle, update the position based on CollidingBody,
            // as it might have experienced some collisions
            CollidingBody collidingBody = collidingBodies.get(entityId);
            if (collidingBody != null) {
                boolean movedHorizontally = Math.abs(collidingBody.oldX - collidingBody.newX) > 0.0001;
                boolean movedVertically = Math.abs(collidingBody.oldY - collidingBody.newY) > 0.0001;

                if (movedHorizontally || movedVertically) {
                    Position2DComponent position = movingEntity.getComponent(Position2DComponent.class);
                    position.setX(collidingBody.newX);
                    position.setY(collidingBody.newY);

                    boolean hadCollision = collidingBody.hadCollisionX || collidingBody.hadCollisionY;
                    if (hadCollision) {
                        MovingComponent moving = movingEntity.getComponent(MovingComponent.class);
                        if (collidingBody.hadCollisionX)
                            moving.setSpeedX(0);
                        else
                            moving.setSpeedY(0);
                        movingEntity.saveChanges();
                    } else {
                        movingEntity.saveChanges();
                    }

                    movingEntity.send(new EntityMoved(hadCollision, collidingBody.oldX, collidingBody.oldY, collidingBody.newX, collidingBody.newY));
                }
            } else {
                Obstacle obstacle = obstacles.get(entityId);
                if (obstacle != null) {
                    boolean movedHorizontally = Math.abs(obstacle.oldX - obstacle.newX) > 0.0001;
                    boolean movedVertically = Math.abs(obstacle.oldY - obstacle.newY) > 0.0001;

                    if (movedHorizontally || movedVertically) {
                        Position2DComponent position = movingEntity.getComponent(Position2DComponent.class);
                        position.setX(obstacle.newX);
                        position.setY(obstacle.newY);
                        movingEntity.saveChanges();

                        if (obstacle.oldX != obstacle.newX
                                || obstacle.oldY != obstacle.newY)
                            movingEntity.send(new EntityMoved(collidingBody.oldX, collidingBody.oldY, collidingBody.newX, collidingBody.newY));
                    }
                }
            }
        }
    }

    private Vector2 collisionTemp = new Vector2();

    private void processCollisions() {
        for (CollidingBody collidingBody : collidingBodies.values()) {
            collidingBody.hadCollisionX = false;
            collidingBody.hadCollisionY = false;
            collidingBody.adjustedX = 0;
            collidingBody.adjustedY = 0;
            for (Obstacle obstacle : obstacles.values()) {
                if (collisionFilter == null)
                    checkForCollision(collidingBody, obstacle);
                else {
                    EntityRef collidingEntity = internalEntityManager.getEntityById(collidingBody.entityId);
                    EntityRef obstacleEntity = internalEntityManager.getEntityById(obstacle.entityId);
                    if (collisionFilter.canCollideWith(collidingEntity, obstacleEntity))
                        checkForCollision(collidingBody, obstacle);
                }
            }
        }
    }

    private void checkForCollision(CollidingBody collidingBody, Obstacle obstacle) {
        Vector2 collisionOverlap = getCollisionOverlap(collidingBody, obstacle, collisionTemp);
        if (collisionOverlap != null) {
            collidingBody.newX += collisionOverlap.x;
            collidingBody.adjustedX += collisionOverlap.x;
            collidingBody.newY += collisionOverlap.y;
            collidingBody.adjustedY += collisionOverlap.y;

            if (Math.abs(collisionOverlap.x) > Math.abs(collisionOverlap.y)) {
                // Adjusting x
                collidingBody.hadCollisionX = true;
            } else {
                // Adjusting y
                collidingBody.hadCollisionY = true;
            }
        }
    }

    /**
     * Returns how to move the object to get out of collision. If null is returned, there is no collision.
     *
     * @param collidingBody
     * @param obstacle
     * @param vectorToUse
     * @return
     */
    private static Vector2 getCollisionOverlap(CollidingBody collidingBody, Obstacle obstacle, Vector2 vectorToUse) {
        float x1Min = collidingBody.newX + collidingBody.left;
        float x1Max = collidingBody.newX + collidingBody.right;
        float y1Min = collidingBody.newY + collidingBody.down;
        float y1Max = collidingBody.newY + collidingBody.up;

        float x2Min = obstacle.newX + obstacle.left;
        float x2Max = obstacle.newX + obstacle.right;
        float y2Min = obstacle.newY + obstacle.down;
        float y2Max = obstacle.newY + obstacle.up;

        if (x1Max > x2Min && x1Min < x2Max
                && y1Max > y2Min && y1Min < y2Max) {
            // There could be an overlap
            if (obstacle.isAABB) {
                float width = Math.min(x1Max, x2Max) - Math.max(x1Min, x2Min);
                if (x1Max - x1Min == width || x2Max - x2Min == width)
                    // The overlap is the whole size of either bodies
                    vectorToUse.x = 0;
                else
                    vectorToUse.x = Math.signum(x1Min - x2Min) * width;
                float height = Math.min(y1Max, y2Max) - Math.max(y1Min, y2Min);
                if (y1Max - y1Min == height || y2Max - y2Min == height)
                    // The overlap is the whole size of either bodies
                    vectorToUse.y = 0;
                else
                    vectorToUse.y = Math.signum(y1Min - y2Min) * height;

                if (vectorToUse.x != 0 && vectorToUse.y != 0) {
                    if (Math.abs(vectorToUse.x) < Math.abs(vectorToUse.y)) {
                        vectorToUse.y = 0;
                    } else {
                        vectorToUse.x = 0;
                    }
                }

                return vectorToUse;
            } else {
                // Check for not AABBs
                // Using the Separating Axis Test
                return SeparatingAxisTest.findOverlap(x1Min, x1Max, y1Min, y1Max,
                        obstacle.nonAABBVertices, vectorToUse);
            }
        } else {
            return null;
        }
    }

    @Override
    public void destroy() {
        internalEntityManager.removeEntityListener(this);
    }

    @Override
    public void entityModified(SimpleEntity entity, Collection<Class<? extends Component>> affectedComponent) {
        int entityId = entity.getEntityId();

        if (isAffected(entity, affectedComponent, CollidingBodyComponent.class))
            updateCollidingBodies(entity, entityId);
        if (isAffected(entity, affectedComponent, ObstacleComponent.class))
            updateObstacles(entity, entityId);
        else if (affectedComponent.contains(Position2DComponent.class)) {
            if (entity.hasComponent(ObstacleComponent.class))
                updateObstacle(entityId);
        }
        if (isAffected(entity, affectedComponent, SensorComponent.class))
            updateSensors(entity, entityId);
        if (isAffected(entity, affectedComponent, SensorTriggerComponent.class))
            updateSensorTriggers(entity, entityId);
        else if (affectedComponent.contains(Position2DComponent.class)) {
            if (entity.hasComponent(SensorTriggerComponent.class))
                updateSensorTrigger(entityId);
        }
    }

    private boolean isAffected(SimpleEntity entity, Collection<Class<? extends Component>> affectedComponent, Class<? extends Component> component) {
        return affectedComponent.contains(component)
                || (
                (affectedComponent.contains(Size2DComponent.class) || affectedComponent.contains(HorizontalOrientationComponent.class))
                        && entity.hasComponent(component));
    }

    private void updateSensorTriggers(SimpleEntity entity, int entityId) {
        SensorTrigger sensorTrigger = sensorTriggers.get(entityId);
        if (sensorTrigger == null && entity.hasComponent(SensorTriggerComponent.class))
            addSensorTrigger(entityId);
        else if (sensorTrigger != null && !entity.hasComponent(SensorTriggerComponent.class))
            removeSensorTrigger(entityId);
        else if (sensorTrigger != null)
            updateSensorTrigger(entityId);
    }

    private void updateObstacles(SimpleEntity entity, int entityId) {
        Obstacle obstacle = obstacles.get(entityId);
        if (obstacle == null && entity.hasComponent(ObstacleComponent.class))
            addObstacle(entityId);
        else if (obstacle != null && !entity.hasComponent(ObstacleComponent.class))
            removeObstacle(entityId);
        else if (obstacle != null)
            updateObstacle(entityId);
    }

    @Override
    public void entitiesModified(Iterable<SimpleEntity> entities) {
        for (SimpleEntity entity : entities) {
            int entityId = entity.getEntityId();

            updateCollidingBodies(entity, entityId);
            updateObstacles(entity, entityId);
            updateSensors(entity, entityId);
            updateSensorTriggers(entity, entityId);
        }
    }

    private void updateSensors(SimpleEntity entity, int entityId) {
        Map<String, Sensor> sensorMap = sensors.get(entityId);
        if (sensorMap == null && entity.hasComponent(SensorComponent.class))
            addSensor(entityId);
        else if (sensorMap != null && !entity.hasComponent(SensorComponent.class))
            removeSensor(entityId);
        else if (sensorMap != null)
            updateSensor(entityId);
    }

    private void updateCollidingBodies(SimpleEntity entity, int entityId) {
        CollidingBody collidingBody = collidingBodies.get(entityId);
        if (collidingBody == null && entity.hasComponent(CollidingBodyComponent.class))
            addCollidingBody(entityId);
        else if (collidingBody != null && !entity.hasComponent(CollidingBodyComponent.class))
            removeCollidingBody(entityId);
        else if (collidingBody != null)
            updateCollidingBody(entityId);
    }

    private float getLeft(Size2DComponent size, HorizontalOrientationComponent horizontalOrientation, float leftPerc, float rightPerc) {
        if (horizontalOrientation == null || horizontalOrientation.isFacingRight()) {
            return PositionResolver.getLeft(size, leftPerc);
        } else {
            return -PositionResolver.getRight(size, rightPerc);
        }
    }

    private float getRight(Size2DComponent size, HorizontalOrientationComponent horizontalOrientation, float leftPerc, float rightPerc) {
        if (horizontalOrientation == null || horizontalOrientation.isFacingRight()) {
            return PositionResolver.getRight(size, rightPerc);
        } else {
            return -PositionResolver.getLeft(size, leftPerc);
        }
    }

    private float getDown(Size2DComponent size, float downPerc) {
        return PositionResolver.getDown(size, downPerc);
    }

    private float getUp(Size2DComponent size, float upPerc) {
        return PositionResolver.getUp(size, upPerc);
    }

    private void addSensorTrigger(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        SensorTriggerComponent st = entity.getComponent(SensorTriggerComponent.class);
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);
        SensorTrigger sensorTrigger;
        if (st.isAABB())
            sensorTrigger = new SensorTrigger(
                    entityId, getLeft(size, orientation, st.getLeftPerc(), st.getRightPerc()), getRight(size, orientation, st.getLeftPerc(), st.getRightPerc()), getDown(size, st.getDownPerc()), getUp(size, st.getUpPerc()));
        else
            sensorTrigger = new SensorTrigger(
                    entityId, st.getNonAABBVertices().getVertices());
        sensorTrigger.updatePosition(position.getX(), position.getY());

        sensorTriggers.put(entityId, sensorTrigger);
    }

    private void updateSensorTrigger(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        SensorTriggerComponent st = entity.getComponent(SensorTriggerComponent.class);
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);

        SensorTrigger sensorTrigger = sensorTriggers.get(entityId);
        sensorTrigger.left = getLeft(size, orientation, st.getLeftPerc(), st.getRightPerc());
        sensorTrigger.right = getRight(size, orientation, st.getLeftPerc(), st.getRightPerc());
        sensorTrigger.down = getDown(size, st.getDownPerc());
        sensorTrigger.up = getUp(size, st.getUpPerc());
        sensorTrigger.isAABB = st.isAABB();
        if (!sensorTrigger.isAABB)
            sensorTrigger.nonAABBVertices = st.getNonAABBVertices().getVertices();
        else
            sensorTrigger.nonAABBVertices = null;

        sensorTrigger.updatePosition(position.getX(), position.getY());
    }

    private void removeSensorTrigger(int entityId) {
        sensorTriggers.remove(entityId);
    }

    private void addSensor(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        SensorComponent sensorComp = entity.getComponent(SensorComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);

        Map<String, Sensor> sensorMap = new HashMap<String, Sensor>();
        for (SensorDef sensorDef : sensorComp.getSensors()) {
            Sensor sensor = new Sensor(entityId, sensorDef.type,
                    getLeft(size, orientation, sensorDef.left, sensorDef.right),
                    getRight(size, orientation, sensorDef.left, sensorDef.right),
                    getDown(size, sensorDef.down),
                    getUp(size, sensorDef.up));
            sensorMap.put(sensor.type, sensor);
        }

        sensors.put(entityId, sensorMap);
    }

    private void updateSensor(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        SensorComponent sens = entity.getComponent(SensorComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);

        Map<String, Sensor> oldSensorMap = sensors.get(entityId);

        Map<String, Sensor> newSensorMap = new HashMap<String, Sensor>();
        for (SensorDef sensorDef : sens.getSensors()) {
            Sensor sensor = oldSensorMap.get(sensorDef.type);
            if (sensor != null) {
                sensor.left = getLeft(size, orientation, sensorDef.left, sensorDef.right);
                sensor.right = getRight(size, orientation, sensorDef.left, sensorDef.right);
                sensor.down = getDown(size, sensorDef.down);
                sensor.up = getUp(size, sensorDef.up);
            } else {
                sensor = new Sensor(entityId, sensorDef.type,
                        getLeft(size, orientation, sensorDef.left, sensorDef.right),
                        getRight(size, orientation, sensorDef.left, sensorDef.right),
                        getDown(size, sensorDef.down),
                        getUp(size, sensorDef.up));
            }
            newSensorMap.put(sensorDef.type, sensor);
        }
        sensors.put(entityId, newSensorMap);
    }

    private void removeSensor(int entityId) {
        sensors.remove(entityId);
    }

    private void addObstacle(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        ObstacleComponent obs = entity.getComponent(ObstacleComponent.class);
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);
        Obstacle obstacle;
        if (obs.isAABB())
            obstacle = new Obstacle(
                    entityId, getLeft(size, orientation, obs.getLeftPerc(), obs.getRightPerc()), getRight(size, orientation, obs.getLeftPerc(), obs.getRightPerc()), getDown(size, obs.getDownPerc()), getUp(size, obs.getUpPerc()));
        else
            obstacle = new Obstacle(
                    entityId, obs.getNonAABBVertices().getVertices());
        obstacle.updatePositions(position.getX(), position.getY(), position.getX(), position.getY());

        obstacles.put(entityId, obstacle);
    }

    private void updateObstacle(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        ObstacleComponent obs = entity.getComponent(ObstacleComponent.class);
        Position2DComponent position = entity.getComponent(Position2DComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);

        Obstacle obstacle = obstacles.get(entityId);
        obstacle.left = getLeft(size, orientation, obs.getLeftPerc(), obs.getRightPerc());
        obstacle.right = getRight(size, orientation, obs.getLeftPerc(), obs.getRightPerc());
        obstacle.down = getDown(size, obs.getDownPerc());
        obstacle.up = getUp(size, obs.getUpPerc());
        obstacle.isAABB = obs.isAABB();
        if (!obstacle.isAABB)
            obstacle.nonAABBVertices = obs.getNonAABBVertices().getVertices();
        else
            obstacle.nonAABBVertices = null;

        obstacle.updatePositions(position.getX(), position.getY(), position.getX(), position.getY());
    }

    private void removeObstacle(int entityId) {
        obstacles.remove(entityId);
    }

    private void addCollidingBody(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        CollidingBodyComponent colBody = entity.getComponent(CollidingBodyComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);
        CollidingBody collidingBody = new CollidingBody(
                entityId, getLeft(size, orientation, colBody.getLeftPerc(), colBody.getRightPerc()), getRight(size, orientation, colBody.getLeftPerc(), colBody.getRightPerc()), getDown(size, colBody.getDownPerc()), getUp(size, colBody.getUpPerc()));

        collidingBodies.put(entityId, collidingBody);
    }

    private void updateCollidingBody(int entityId) {
        EntityRef entity = internalEntityManager.getEntityById(entityId);
        CollidingBodyComponent colBody = entity.getComponent(CollidingBodyComponent.class);
        Size2DComponent size = entity.getComponent(Size2DComponent.class);
        HorizontalOrientationComponent orientation = entity.getComponent(HorizontalOrientationComponent.class);

        CollidingBody collidingBody = collidingBodies.get(entityId);
        collidingBody.left = getLeft(size, orientation, colBody.getLeftPerc(), colBody.getRightPerc());
        collidingBody.right = getRight(size, orientation, colBody.getLeftPerc(), colBody.getRightPerc());
        collidingBody.down = getDown(size, colBody.getDownPerc());
        collidingBody.up = getUp(size, colBody.getUpPerc());
    }

    private void removeCollidingBody(int entityId) {
        collidingBodies.remove(entityId);
    }
}
