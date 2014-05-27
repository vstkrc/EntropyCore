package us.rockhopper.entropy.screen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import us.rockhopper.entropy.entities.BasicShip;
import us.rockhopper.entropy.entities.Cockpit;
import us.rockhopper.entropy.entities.Gyroscope;
import us.rockhopper.entropy.entities.Thruster;
import us.rockhopper.entropy.gui.PartImageButton;
import us.rockhopper.entropy.utility.FileIO;
import us.rockhopper.entropy.utility.Layout;
import us.rockhopper.entropy.utility.Part;
import us.rockhopper.entropy.utility.PartClassAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gson.GsonBuilder;

public class ShipEditor implements Screen {

	private Stage stage;
	private Skin skin;
	private OrthographicCamera camera;
	private SpriteBatch batch;

	private Table selections;
	private Table tabbed;
	private Table gridTable;
	private Table info;

	private String defaultFolder = new JFileChooser().getFileSystemView()
			.getDefaultDirectory().toString();

	private ArrayList<Part> command = new ArrayList<Part>();
	private ArrayList<Part> control = new ArrayList<Part>();
	private ArrayList<Part> thrust = new ArrayList<Part>();
	private ArrayList<Part> hull = new ArrayList<Part>();
	private ArrayList<Part> weaponry = new ArrayList<Part>();

	private Part activePart;
	private float activePartX, activePartY;
	private int sWidth, sHeight;

	ArrayList<PartImageButton> grid = new ArrayList<PartImageButton>();
	ArrayList<Part> parts = new ArrayList<Part>();
	private Layout setup;
	private Image activeImage;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (activePart != null) {
			Sprite sprite = new Sprite(new Texture("assets/img/overlay.png"));
			sprite.setPosition(activePartX - sWidth / 2, activePartY - sHeight
					/ 2);
			sprite.draw(batch);
		}
		batch.end();

		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		selections.invalidateHierarchy();
		tabbed.invalidateHierarchy();
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		sWidth = width;
		sHeight = height;
	}

	@Override
	public void show() {
		// Initialize input processing
		InputMultiplexer multiplexer = new InputMultiplexer();
		stage = new Stage();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (activePart != null && activeImage != null
						&& keycode == Keys.Q) {
					activeImage.setRotation(activeImage.getRotation() + 90);
					activePart.rotateLeft();
				}
				if (activePart != null && activeImage != null
						&& keycode == Keys.E) {
					activeImage.setRotation(activeImage.getRotation() - 90);
					activePart.rotateRight();
				}
				return true;
			}
		});
		Gdx.input.setInputProcessor(multiplexer);

		// Begin initializing the layout.
		setup = new Layout(1, 1);

		// Sprite rendering
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		// Load all command parts into its list.
		String commandPath = defaultFolder + "\\EntropyShips\\Parts\\Command\\";
		for (File file : FileIO.getFilesForFolder(new File(commandPath))) {
			String partJSON = FileIO.read(file.getAbsolutePath());
			GsonBuilder gson = new GsonBuilder();
			gson.registerTypeAdapter(Part.class, new PartClassAdapter());
			Part part = gson.create().fromJson(partJSON, Part.class);
			command.add(part);
		}

		// Load all control parts into its list.
		String controlPath = defaultFolder + "\\EntropyShips\\Parts\\Control\\";
		for (File file : FileIO.getFilesForFolder(new File(controlPath))) {
			String partJSON = FileIO.read(file.getAbsolutePath());
			GsonBuilder gson = new GsonBuilder();
			gson.registerTypeAdapter(Part.class, new PartClassAdapter());
			Part part = gson.create().fromJson(partJSON, Part.class);
			control.add(part);
		}

		// Load all thrust parts into its list.
		String thrustPath = defaultFolder + "\\EntropyShips\\Parts\\Thrust\\";
		for (File file : FileIO.getFilesForFolder(new File(thrustPath))) {
			String partJSON = FileIO.read(file.getAbsolutePath());
			GsonBuilder gson = new GsonBuilder();
			gson.registerTypeAdapter(Part.class, new PartClassAdapter());
			Part part = gson.create().fromJson(partJSON, Part.class);
			thrust.add(part);
		}

		// Load all hull parts into its list.
		String hullPath = defaultFolder + "\\EntropyShips\\Parts\\Hull\\";
		for (File file : FileIO.getFilesForFolder(new File(hullPath))) {
			String partJSON = FileIO.read(file.getAbsolutePath());
			GsonBuilder gson = new GsonBuilder();
			gson.registerTypeAdapter(Part.class, new PartClassAdapter());
			Part part = gson.create().fromJson(partJSON, Part.class);
			hull.add(part);
		}

		// Load all weapon parts into its list.
		String weaponPath = defaultFolder + "\\EntropyShips\\Parts\\Weaponry\\";
		for (File file : FileIO.getFilesForFolder(new File(weaponPath))) {
			String partJSON = FileIO.read(file.getAbsolutePath());
			GsonBuilder gson = new GsonBuilder();
			gson.registerTypeAdapter(Part.class, new PartClassAdapter());
			Part part = gson.create().fromJson(partJSON, Part.class);
			weaponry.add(part);
		}

		skin = new Skin(Gdx.files.internal("assets/ui/uiskin.json"),
				new TextureAtlas("assets/ui/uiskin.pack"));

		selections = new Table(skin);
		gridTable = new Table(skin);
		info = new Table(skin);
		tabbed = new Table(skin);
		gridTable.setFillParent(true);
		selections.setFillParent(true);
		selections.debug();

		final TextButton buttonCommand = new TextButton("Command", skin,
				"default");
		final TextButton buttonControl = new TextButton("Control", skin,
				"default");
		final TextButton buttonThrust = new TextButton("Thrust", skin,
				"default");
		final TextButton buttonHull = new TextButton("Hull", skin, "default");
		final TextButton buttonWeaponry = new TextButton("Weaponry", skin,
				"default");
		final TextButton buttonGo = new TextButton("Start", skin, "default");

		final ClickListener partAddListener = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (activePart != null) {
					if (grid.size() == 1 && !(activePart instanceof Cockpit)) {
						new Dialog("", skin) {
							{
								text("The first piece on your ship must be a command module.");
								button("Okay");
							}
						}.show(stage);
					} else {
						// This is the tile clicked.
						PartImageButton active = (PartImageButton) event
								.getListenerActor();
						boolean match = false;
						if (grid.size() != 1) {
							System.out.println("The number of nodes is "
									+ activePart.getAttachmentNodes().length);
							// Create a new array
							int[] newArray = new int[activePart
									.getAttachmentNodes().length];
							for (int i = 0; i < activePart.getAttachmentNodes().length; ++i) {
								newArray[i] = new Integer(
										activePart.getAttachmentNodes()[i]);
								System.out.println("Added a value of "
										+ activePart.getAttachmentNodes()[i]);
							}

							// If this is a valid means of attaching a piece
							for (int k = 0; k < 4; ++k) {
								if (hasMatch(newArray,
										getAdjacent(grid, active, k), k)) {
									System.out.println("Ran!");
									match = true;
								}
							}
						} else {
							System.out
									.println("The attachment node should still be 2: "
											+ activePart.getAttachmentNodes()[0]);
							match = true;
						}
						if (match) {
							// For every valid node...
							for (int j = 0; j < activePart.getAttachmentNodes().length; ++j) {
								int[] nodes = activePart.getAttachmentNodes();
								// Add additional ship piece slots.
								PartImageButton extra = new PartImageButton(
										null,
										new Texture("assets/img/grid.png"));
								// Give these slots any listeners associated
								// with the clicked tile.
								for (int i = 0; i < active.getListeners().size; ++i) {
									extra.addListener(active.getListeners()
											.get(i));
								}
								Vector2 buttonCoords = new Vector2(
										active.getX(), active.getY());
								if (nodes[j] == 0
										&& !hasAdjacent(grid, active, 0)) {
									extra.setPosition(buttonCoords.x,
											buttonCoords.y + active.getHeight());
									grid.add(extra);
									stage.addActor(extra);
								} else if (nodes[j] == 1
										&& !hasAdjacent(grid, active, 1)) {
									extra.setPosition(
											buttonCoords.x + active.getWidth(),
											buttonCoords.y);
									grid.add(extra);
									stage.addActor(extra);
								} else if (nodes[j] == 2
										&& !hasAdjacent(grid, active, 2)) {
									System.out
											.println("Just making sure this is run.");
									extra.setPosition(buttonCoords.x,
											buttonCoords.y - active.getHeight());
									grid.add(extra);
									stage.addActor(extra);
								} else if (nodes[j] == 3
										&& !hasAdjacent(grid, active, 3)) {
									extra.setPosition(
											buttonCoords.x - active.getWidth(),
											buttonCoords.y);
									grid.add(extra);
									stage.addActor(extra);
								}
							}

							int[] newArray = new int[activePart
									.getAttachmentNodes().length];
							for (int i = 0; i < activePart.getAttachmentNodes().length; ++i) {
								newArray[i] = new Integer(
										activePart.getAttachmentNodes()[i]);
								System.out.println("Added a value of "
										+ activePart.getAttachmentNodes()[i]);
							}

							// Now modify the clicked tile
							PartImageButton temp = new PartImageButton(
									activePart.clone().setAttachmentNodes(
											newArray), new Texture(
											activePart.getSprite()));
							temp.setOrigin(temp.getWidth() / 2f,
									temp.getHeight() / 2f);
							temp.setRotation(activePart.getRotation());
							temp.setPosition(active.getX(), active.getY());
							active.remove();
							grid.remove(active);
							active = temp;
							grid.add(active);
							stage.addActor(active);
							System.out.println(activePart.getName() + " "
									+ activePart.getSprite() + " "
									+ activePart.getAttachmentNodes()[0]);
						} else {
							new Dialog("", skin) {
								{
									text("Those pieces don't line up that way.");
									button("Okay");
								}
							}.show(stage);
						}
					}
				}
			}
		};

		final ClickListener itemChooseListener = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				PartImageButton active = (PartImageButton) event
						.getListenerActor();
				activePart = active.getPart();
				activePart.clearRotation();
				Vector2 buttonCoords = active
						.localToStageCoordinates(new Vector2(0, 0));
				activePartX = buttonCoords.x;
				activePartY = buttonCoords.y;
				info.clear();
				Label heading = new Label(activePart.getName(), skin, "default");
				Label description = new Label(activePart.getDescription(),
						skin, "default");
				description.setWrap(true);
				info.add(heading);
				info.row();
				info.add("Cost: " + activePart.getCost());
				info.row();
				info.add(description).width(200).center();
				info.row();
				Image activeSprite = new Image(new Texture(
						activePart.getSprite()));
				activeSprite.setOrigin(activeSprite.getWidth() / 2f,
						activeSprite.getHeight() / 2f);
				activeImage = activeSprite;
				info.add(activeSprite);
			}
		};

		ClickListener tabChooseListener = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (event.getListenerActor() == buttonCommand) {
					tabbed.clear();
					info.clear();
					activePart = null;
					for (int i = 0; i < command.size(); ++i) {
						Part part = command.get(i);
						PartImageButton selectPart = new PartImageButton(part,
								new Texture(part.getSprite()));
						selectPart.addListener(itemChooseListener);
						tabbed.add(selectPart);
					}
				} else if (event.getListenerActor() == buttonControl) {
					tabbed.clear();
					info.clear();
					activePart = null;
					for (int i = 0; i < control.size(); ++i) {
						Part part = control.get(i);
						PartImageButton selectPart = new PartImageButton(part,
								new Texture(part.getSprite()));
						selectPart.addListener(itemChooseListener);
						tabbed.add(selectPart);
					}
				} else if (event.getListenerActor() == buttonThrust) {
					tabbed.clear();
					info.clear();
					activePart = null;
					for (int i = 0; i < thrust.size(); ++i) {
						Part part = thrust.get(i);
						PartImageButton selectPart = new PartImageButton(part,
								new Texture(part.getSprite()));
						selectPart.addListener(itemChooseListener);
						tabbed.add(selectPart);
					}
				} else if (event.getListenerActor() == buttonHull) {
					tabbed.clear();
					info.clear();
					activePart = null;
					for (int i = 0; i < hull.size(); ++i) {
						Part part = hull.get(i);
						PartImageButton selectPart = new PartImageButton(part,
								new Texture(part.getSprite()));
						selectPart.addListener(itemChooseListener);
						tabbed.add(selectPart);
					}
				} else if (event.getListenerActor() == buttonWeaponry) {
					tabbed.clear();
					info.clear();
					activePart = null;
					for (int i = 0; i < weaponry.size(); ++i) {
						Part part = weaponry.get(i);
						PartImageButton selectPart = new PartImageButton(part,
								new Texture(part.getSprite()));
						selectPart.addListener(itemChooseListener);
						tabbed.add(selectPart);
					}
				}
				// Instantiate the ship and move onto the next screen.
				else if (event.getListenerActor() == buttonGo) {
					// Declare textures
					Cockpit partCockpit = new Cockpit(new Vector2(1, 3), 1, 1,
							0.8f, "assets/img/sampleShip.png");
					Gyroscope partGyro = new Gyroscope(new Vector2(1, 2), 1, 1,
							0.8f, "assets/img/gyroscope.png")
							.setClockwise(Keys.D).setCounterClockwise(Keys.A)
							.setStrength(5);
					Thruster partThruster = new Thruster(new Vector2(1, 0), 1,
							1, 0.8f, "assets/img/thruster.png")
							.setForward(Keys.W).setReverse(Keys.S)
							.setCanReverse(true).setStrength(1);
					Thruster partThruster1 = new Thruster(new Vector2(1, 1), 1,
							1, 0.8f, "assets/img/thruster.png")
							.setForward(Keys.W).setReverse(Keys.S)
							.setCanReverse(true).setStrength(1);
					parts.add(partCockpit);
					parts.add(partGyro);
					parts.add(partThruster);
					parts.add(partThruster1);
					Layout setup = new Layout(2, 4);
					setup.setPart(partCockpit, partCockpit.getGridX(),
							partCockpit.getGridY());
					setup.setPart(partGyro, partGyro.getGridX(),
							partGyro.getGridY());
					setup.setPart(partThruster1, partThruster1.getGridX(),
							partThruster1.getGridY());
					setup.setPart(partThruster, partThruster.getGridX(),
							partThruster.getGridY());
					BasicShip ship = new BasicShip(new Vector2(1, 3), 1, 3,
							parts, setup);

					// Serialize and write to file
					GsonBuilder gson = new GsonBuilder();
					gson.registerTypeAdapter(Part.class, new PartClassAdapter());
					String shipJSON = gson.setPrettyPrinting().create()
							.toJson(ship);
					FileIO.write(defaultFolder + "\\EntropyShips\\"
							+ "sample.json", shipJSON);

					// Switch screens
					((Game) Gdx.app.getApplicationListener())
							.setScreen(new GameStart());
				}
			}
		};

		buttonCommand.addListener(tabChooseListener);
		buttonControl.addListener(tabChooseListener);
		buttonThrust.addListener(tabChooseListener);
		buttonHull.addListener(tabChooseListener);
		buttonWeaponry.addListener(tabChooseListener);
		buttonGo.addListener(tabChooseListener);
		selections.left().top();
		selections.add(buttonCommand);
		selections.add(buttonControl);
		selections.add(buttonThrust);
		selections.add(buttonHull);
		selections.add(buttonWeaponry);
		selections.add(buttonGo);
		selections.row();
		selections.add(tabbed).colspan(5);
		selections.row();
		selections.add(info).colspan(5);
		
		// Add the initial ship slot
		PartImageButton addPart = new PartImageButton(null, new Texture(
				"assets/img/grid.png"));
		addPart.addListener(partAddListener);
		gridTable.add(addPart).center();
		grid.add(addPart);
		stage.addActor(gridTable);
		stage.addActor(selections);

		stage.addAction(sequence(moveTo(0, stage.getHeight()),
				moveTo(0, 0, .5f))); // coming in from top animation
	}

	/**
	 * Determines whether or not any of the nodes in these two lists are able to
	 * attach together.
	 * 
	 * @param attachmentNodes
	 * @param adjacent
	 * @return
	 */
	protected boolean hasMatch(int[] base, Part adjacent, int direction) {
		if (adjacent == null) {
			return false;
		}

		System.out.println("Running HASMATCH");
		System.out.println("The adjacent piece is " + adjacent.getName());
		boolean result = false;
		for (int i = 0; i < base.length; ++i) {
			int node = base[i];
			System.out.println("The attachment node for your piece now is "
					+ node);
			switch (node) {
			case 0:
				System.out.println("Checking above!");
				for (int j = 0; j < adjacent.getAttachmentNodes().length; ++j) {
					System.out.println(adjacent.getName());
					System.out
							.println("The node you are trying to attach to has points at "
									+ adjacent.getAttachmentNodes()[j]);
					if (direction == node
							&& adjacent.getAttachmentNodes()[j] == 2) {
						System.out.println("Can attach to the above node");
						result = true;
						break;
					} // TODO: still some weird rotation problems, cleanup this
						// section, remove TODO's, look into other potential
						// ways of doing this? Who knows. Remove all debugs.
						// Serialize. Add all other parts and thoroughly test
						// ship builder.
				}
				break;
			case 1:
				System.out.println("Checking right!");
				for (int j = 0; j < adjacent.getAttachmentNodes().length; ++j) {
					System.out.println(adjacent.getName());
					System.out
							.println("The node you are trying to attach to has points at "
									+ adjacent.getAttachmentNodes()[j]);
					if (direction == node
							&& adjacent.getAttachmentNodes()[j] == 3) {
						System.out.println("Can attach to the right node");
						result = true;
						break;
					}
				}
				break;
			case 2: // Attachment node for the piece being attached is 2
				System.out.println("Checking below!");
				for (int j = 0; j < adjacent.getAttachmentNodes().length; ++j) {
					System.out.println(adjacent.getName());
					System.out
							.println("The node you are trying to attach to has points at "
									+ adjacent.getAttachmentNodes()[j]);
					if (direction == node
							&& adjacent.getAttachmentNodes()[j] == 0) {
						System.out.println("Can attach to the bottom node");
						result = true;
						break;
					}
				}
				break;
			case 3:
				System.out.println("Checking left!");
				for (int j = 0; j < adjacent.getAttachmentNodes().length; ++j) {
					System.out.println(adjacent.getName());
					System.out
							.println("The node you are trying to attach to has points at "
									+ adjacent.getAttachmentNodes()[j]);
					if (direction == node
							&& adjacent.getAttachmentNodes()[j] == 1) {
						System.out.println("Can attach to the left node");
						result = true;
						break;
					}
				}
				break;
			}
		}
		System.out.println("Returning " + result);
		return result;
	}

	/**
	 * Gets the imageButton adjacent to the selected button in the direction
	 * given.
	 * 
	 * @param grid2
	 *            The list of pieces to check.
	 * @param active
	 *            The piece to check the adjacent nature for.
	 * @param direction
	 *            The direction to look in.
	 * @return A list of the pieces which are adjacent. Returns null if no
	 *         adjacent are found.
	 */
	// TODO THis is where the problem is. It always is null.
	protected Part getAdjacent(ArrayList<PartImageButton> grid2,
			PartImageButton active, int direction) {
		Vector2 activeCoords = active.localToStageCoordinates(new Vector2(
				active.getWidth() / 2f, active.getHeight() / 2f));
		for (int i = 0; i < grid.size(); ++i) {
			PartImageButton button = grid.get(i);
			System.out.println("Adjacent? " + hasAdjacent(grid, button, 2));
			if (!button.equals(active)) {
				Vector2 buttonCoords = button
						.localToStageCoordinates(new Vector2(
								button.getWidth() / 2f, button.getHeight() / 2f));
				if (direction == 1
						&& activeCoords.x + button.getWidth() == buttonCoords.x
						&& activeCoords.y == buttonCoords.y) {
					return button.getPart();
				} else if (direction == 0
						&& activeCoords.x == buttonCoords.x
						&& activeCoords.y + button.getHeight() == buttonCoords.y) {
					return button.getPart();
				} else if (direction == 2
						&& activeCoords.x == buttonCoords.x
						&& activeCoords.y - button.getHeight() == buttonCoords.y) {
					return button.getPart();
				} else if (direction == 3
						&& activeCoords.x - button.getWidth() == buttonCoords.x
						&& activeCoords.y == buttonCoords.y) {
					return button.getPart();
				}
			}
		}
		System.out.println("Returning null.");
		return null;
	}

	/**
	 * Returns whether or not the PartImageButton in the grid has an adjacent
	 * PartImageButton in the given direction.
	 * 
	 * @param grid
	 *            The grid to search for parts in.
	 * @param active
	 *            The PartImageButton of the grid to target.
	 * @param direction
	 *            The direction to search for adjacent buttons--0 for up, 1 for
	 *            right, 2 for down, 3 for left.
	 * @return Whether or not the piece has an adjacent piece.
	 */
	protected boolean hasAdjacent(ArrayList<PartImageButton> grid,
			PartImageButton active, int direction) {
		boolean result = false;
		Vector2 activeCoords = active.localToStageCoordinates(new Vector2(
				active.getWidth() / 2f, active.getHeight() / 2f));
		for (int i = 0; i < grid.size(); ++i) {
			PartImageButton button = grid.get(i);
			Vector2 buttonCoords = button.localToStageCoordinates(new Vector2(
					button.getWidth() / 2f, button.getHeight() / 2f));
			if (direction == 1
					&& activeCoords.x + button.getWidth() == buttonCoords.x
					&& activeCoords.y == buttonCoords.y) {
				result = true;
			} else if (direction == 0 && activeCoords.x == buttonCoords.x
					&& activeCoords.y + button.getHeight() == buttonCoords.y) {
				result = true;
			} else if (direction == 2 && activeCoords.x == buttonCoords.x
					&& activeCoords.y - button.getHeight() == buttonCoords.y) {
				result = true;
			} else if (direction == 3
					&& activeCoords.x - button.getWidth() == buttonCoords.x
					&& activeCoords.y == buttonCoords.y) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
}
