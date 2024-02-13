import java.awt.*;

public class PlayerBullet extends Sprite2D{

    public PlayerBullet(Image i, int windowWidth){
        super(i, i);

    }

    public boolean move(){
        y -= 8;
        if(y <= 0){
            isAlive = false;
        }
        return isAlive;
    }
}
