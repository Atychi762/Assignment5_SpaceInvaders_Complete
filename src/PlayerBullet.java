import java.awt.*;

public class PlayerBullet extends Sprite2D{

    public PlayerBullet(Image i, int windowWidth){
        super(i, i);

    }

    public boolean move(){
        // setting the bullet to move up by 8 pixels every frame
        y -= 8;
        if(y <= 0){
            isAlive = false;
        }
        return isAlive;
    }
}
