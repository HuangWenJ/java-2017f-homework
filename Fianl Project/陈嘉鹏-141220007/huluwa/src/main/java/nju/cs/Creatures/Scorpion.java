package nju.cs.Creatures;

import nju.cs.Screen.Point2D;
import nju.cs.Screen.Screen;
import nju.cs.State.State;
import nju.cs.State.StateType;
import nju.cs.State.Stateinfo;
import nju.cs.lib.Hlib;
import nju.cs.myEnum.DirectionMap;
import nju.cs.myEnum.ECreatureType;
import nju.cs.myEnum.EDIRECTION;
import nju.cs.myImage.myImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjiapeng
 * @Description
 * @date 2018-01-08
 */
public class Scorpion extends Creature {

    EDIRECTION moveDirection = EDIRECTION.西;
    Creature nearestCreature;
    Creature nearestEnemy;

    Creature grandpa;

    public Scorpion(Integer blood, Integer magic, Integer priority, myImage image, Screen screen, Integer id) {
        super(blood, magic, priority, image, screen, ECreatureType.蝎子, id);
    }

    public void setMoveDirection(EDIRECTION moveDirection) {
        this.moveDirection = moveDirection;
    }

    public EDIRECTION getMoveDirection() {
        return moveDirection;
    }

    @Override
    public String getCreatureName(){
        return "蝎子";
    }

    @Override
    public boolean isEnemy(Creature creature){
        return  creature.geteCreatureType() != ECreatureType.小兵 &&
                creature.geteCreatureType() != ECreatureType.蝎子 &&
                creature.geteCreatureType() != ECreatureType.蛇精;
    }


    @Override
    public void observe(Screen screen){
        ArrayList<Creature> creatureArrayList = screen.getCreatureArrayList();
        nearestCreature = Hlib.nearestCreature(this, creatureArrayList);
        nearestEnemy = Hlib.nearestEnemy(this, creatureArrayList);

        List<Creature> lgrandpa = Hlib.findTypeFromList(creatureArrayList, ECreatureType.爷爷);
        grandpa = lgrandpa.size() == 1 ? lgrandpa.get(0) : null;

    }

    @Override
    public void action(){
        if (Hlib.distanceBetween(this.getPosition(), nearestEnemy.getPosition()) < 2){
            attack();
        }
        else if (grandpa != null && Hlib.distanceBetween(this.getPosition(), grandpa.getPosition()) > 1 ){
            moveDirection = Hlib.toDirection(this, grandpa);
            move();
        }
        else{
            if (Hlib.distanceBetween(this.getPosition(), nearestEnemy.getPosition()) > 1){
                moveDirection = Hlib.toDirection(this, nearestEnemy);
                move();
            }
            else{
                attack();
            }
        }
        try{
            Thread.sleep(1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void move(){
        Point2D direction = DirectionMap.EDirection2Point2D(this.moveDirection);
        if (    position.getX() + direction.getX() < screen.getScope().getX() &&
                position.getY() + direction.getY() < screen.getScope().getY() &&
                position.getX() + direction.getX() >= 0 &&
                position.getY() + direction.getY() >= 0 ) {
            Stateinfo sInfo = new Stateinfo();
            sInfo.setMoveStep(direction);
            State s = new State(StateType.移动, sInfo, this);
            screen.pushStateMsg(s);
        }
    }

    @Override
    public void attack(){
        if (Hlib.distanceBetween(this.position, nearestEnemy.getPosition()) >= 2)
            return;
        Stateinfo sinfo = new Stateinfo();
        sinfo.setBlood(random.nextInt(100)+120);
        State s = new State(StateType.减血, sinfo, nearestEnemy);
        screen.pushStateMsg(s);
    }

    @Override
    public boolean isAlive(){
        return this.Blood > 0;
    }

    @Override
    public void die(){
        synchronized (creatureNum){
            creatureNum--;
        }
        logger.info(getCreatureName() + ": died.");
    }

}
