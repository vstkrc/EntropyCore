package us.rockhopper.entropy.entities;

import net.dermetfan.utils.libgdx.graphics.Box2DSprite;
import us.rockhopper.entropy.utility.Part;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class MissileProjectile extends Part{

	private float angle;
	private final float THRUST = 40;
	private Vector2 position;
	private World world;
	
	public MissileProjectile(int gridX, int gridY, float height, float width,
			float density, String sprite, float angle, Vector2 pos) {
		super(gridX, gridY, height, width, density, sprite);
		this.angle = angle;
		position.set(pos);
	}
	
	public void update() {
			this.getBody().applyForceToCenter(
					new Vector2((float) Math.sin(this.getBody().getAngle())
							* -1 * THRUST, (float) Math.cos(this.getBody()
							.getAngle()) * THRUST), true);
	}
	
	public void create() {
		Body body;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.active = true;
		bodyDef.fixedRotation = false;
		bodyDef.angle = angle;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0;
		fixtureDef.density = 2;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(this.getWidth()/2f,this.getHeight()/2f);
		fixtureDef.shape = shape;
		
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef).setUserData(
				new Box2DSprite(new Sprite(new Texture(this.getSprite()))));
		this.setBody(body);
		
		shape.dispose();
	}
	
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public int[] getKeys() {
		return new int[0];
	}

	@Override
	public void trigger(int key) {
	}

	@Override
	public void unTrigger(int key) {
	}
}
