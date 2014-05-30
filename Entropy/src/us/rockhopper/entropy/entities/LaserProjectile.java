package us.rockhopper.entropy.entities;

import net.dermetfan.utils.libgdx.graphics.Box2DSprite;
import us.rockhopper.entropy.utility.Part;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class LaserProjectile extends Part{

	private float angle;
	private World world;
	
	public LaserProjectile(int gridX, int gridY, int height, int width,
			float density, String sprite, float angle) {
		super(gridX, gridY, height, width, density, sprite);
		this.angle = angle;
	}
	
	public void update() {
	}
	
	public void create() {
		Body body;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.active = true;
		bodyDef.fixedRotation = true;
		bodyDef.angle = angle;
		bodyDef.gravityScale = 0;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0;
		fixtureDef.density = 0;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(this.getWidth()/2f,this.getHeight()/2f);
		fixtureDef.shape = shape;
		
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef).setUserData(
				new Box2DSprite(new Sprite(new Texture(this.getSprite()))));
		this.setBody(body);
		
		this.getBody().applyAngularImpulse(40, true);
		
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