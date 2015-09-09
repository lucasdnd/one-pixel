package com.lucasdnd.onepixel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.lucasdnd.onepixel.gameplay.Player;
import com.lucasdnd.onepixel.gameplay.world.World;
import com.lucasdnd.onepixel.ui.SideBar;
import com.lucasdnd.onepixel.ui.Tooltip;

public class OnePixel extends ApplicationAdapter {
	
	// General stuff
	public final static String GAME_NAME = "One Pixel";
	public final static String VERSION = "v0.2.0";
	boolean debug = false;
	
	// Rendering, font
	SpriteBatch fontBatch;
	Tooltip tooltip;
	FontUtils font;
	public static float PIXEL_SIZE = 8f;
	private final float MIN_PIXEL_SIZE = 2f;
	private final float MAX_PIXEL_SIZE = 32f;
	
	// Game objects
	World world;
	Player player;
	SideBar sideBar;
	
	// Input, camera
	InputHandler input;
	OrthographicCamera camera;
	ShapeRenderer shapeRenderer;
	ShapeRenderer uiShapeRenderer;
	
	boolean creatingWorld = false;
	
	@Override
	public void create () {
		
		// Render, camera
		shapeRenderer = new ShapeRenderer();
		uiShapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fontBatch = new SpriteBatch();
		font = new FontUtils();
		
		// Input
		input = new InputHandler();
		Gdx.input.setInputProcessor(input);

		// UI
		int sideBarWidth = 400;
		sideBar = new SideBar(Gdx.graphics.getWidth() - sideBarWidth, 0, sideBarWidth);
		tooltip = new Tooltip();
		
		startNewGame();
	}
	
	public void startNewGame() {
		creatingWorld = true;
	}
	
	private void handleInput() {
		
		// Movement delay
		input.movementDelay--;
		if (input.movementDelay <= 0) {
			input.movementDelay = 0;
		
			// Movement
			if (input.upPressed) {
				
				if (input.shiftPressed) {
					player.faceUp();
				} else {
					if (player.canMoveUp(world)) {
						player.moveUp();
						input.applyMovementDelay();
					}
					if (input.ctrlPressed == false) {
						player.faceUp();
					}
				}
			}
			if (input.downPressed) {
				
				if (input.shiftPressed) {
					player.faceDown();
				} else {
					if (player.canMoveDown(world)) {
						player.moveDown();
						input.applyMovementDelay();
					}
					if (input.ctrlPressed == false) {
						player.faceDown();
					}
				}
			}
			if (input.leftPressed) {
				
				if (input.shiftPressed) {
					player.faceLeft();
				} else {
					if (player.canMoveLeft(world)) {
						player.moveLeft();
						input.applyMovementDelay();
					}
					if (input.ctrlPressed == false) {
						player.faceLeft();
					}
				}
			}
			if (input.rightPressed) {
				
				if (input.shiftPressed) {
					player.faceRight();
				} else {
					if (player.canMoveRight(world)) {
						player.moveRight();
						input.applyMovementDelay();
					}
					if (input.ctrlPressed == false) {
						player.faceRight();
					}
				}
			}
		}
		
		// Action delay
		input.actionDelay--;
		if (input.actionDelay <= 0) {
			input.actionDelay = 0;

			// Action
			if (input.ePressed) {
				player.performAction(world);
				input.applyActionDelay();
			}
		}
		
		// Use delay
		input.useDelay --;
		if (input.useDelay <= 0) {
			input.useDelay = 0;
			
			// Use
			if (input.wPressed) {
				player.useItem(world);
				input.applyUseDelay();
			}
		}
		
		// Zoom control
		if (Gdx.input.isKeyJustPressed(Keys.EQUALS)) {
			PIXEL_SIZE *= 2f;
			if (PIXEL_SIZE >= MAX_PIXEL_SIZE) {
				PIXEL_SIZE = MAX_PIXEL_SIZE;
			}
		} else if (Gdx.input.isKeyJustPressed(Keys.MINUS)) {
			PIXEL_SIZE /= 2f;
			if (PIXEL_SIZE <= MIN_PIXEL_SIZE) {
				PIXEL_SIZE = MIN_PIXEL_SIZE;
			}
		}
		
		// Debug
		if (Gdx.input.isKeyJustPressed(Keys.F3)) {
			debug = !debug;
		}
	}
	
	private void update() {
		
		// Loading new game
		if (creatingWorld) {
			world = new World();
			player = new Player(world);
			creatingWorld = false;
		}
		
		// Normal game loop
		if (player.isDead()) {
			return;
		}
		
		handleInput();
		
		camera.position.set(player.getX() * OnePixel.PIXEL_SIZE + (sideBar.getWidth() * 0.5f), player.getY() * OnePixel.PIXEL_SIZE, 0f);
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		world.update();
		player.update();
		sideBar.update();
		
		handleInputEnd();
	}

	@Override
	public void render () {
		this.update();
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (creatingWorld) {
			sideBar.render(uiShapeRenderer);
			String text = "Creating new world...";
			float space = Gdx.graphics.getWidth() / 2f - sideBar.getWidth() / 2f;
			font.drawWhiteFont(text, space, Gdx.graphics.getHeight() / 2f - 12f, true, Align.center, 0);
			return;
		}
		
		world.render(shapeRenderer);
		player.render(shapeRenderer);
		sideBar.render(uiShapeRenderer);
		
		tooltip.render();
		
		if (player.isDead()) {
			font.drawRedFont("You died", 0f, Gdx.graphics.getHeight() / 2f, false, Align.center, Gdx.graphics.getWidth() - sideBar.getWidth());
		}
		
		// Debug
		if (debug) {
			fontBatch.begin();
			Resources.get().whiteFont.draw(fontBatch, "x: " + player.getX(), 0f, Gdx.graphics.getHeight());
			Resources.get().whiteFont.draw(fontBatch, "y: " + player.getY(), 0f, Gdx.graphics.getHeight() - 20f);
			Resources.get().whiteFont.draw(fontBatch, "world size: " + world.getSize() + "x" + world.getSize(), 0f, Gdx.graphics.getHeight() - 40f);
			fontBatch.end();
		}
	}
	
	public void handleInputEnd() {
		// Mouse just clicked reset
		input.leftMouseJustClicked = false;
		input.rightMouseJustClicked = false;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public World getWorld() {
		return world;
	}

	public SideBar getSideBar() {
		return sideBar;
	}

	public Tooltip getTooltip() {
		return tooltip;
	}
	
	public InputHandler getInputHandler() {
		return input;
	}
}
