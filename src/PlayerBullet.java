import java.awt.*;

public class PlayerBullet extends Sprite2D{

    public PlayerBullet(Image i, int windowWidth){
        super(i);
    }

    public boolean move(){
        y -= 8;
        return true;
    }
}
