package com.crackedcarrot;

import com.crackedcarrot.textures.TextureData;

public class Sprite{
	//Variable used to determine what kind of sprite this is.
	//And what subtype. This is needed to avoid loading lots of
	//VBOs needlessly. if two sprites are just the same
	//they can share the same VBO.
	private int type;
	private int subType;
    // Position.
    public float x;
    public float y;
    public float z;
    // Is the sprite going to be draw'd or not?
    public boolean draw = true;
    // color and opacity and for this sprite.
    public float r = 1.0f;
    public float g = 1.0f;
    public float b = 1.0f;
    public float opacity = 1.0f;
    //This needs to be here for the sake of animated sprites.
    //It makes the implementation of animated sprites mutch simpler.
	private int cFrame;
    // Size.
    private float width;
    private float height;
    //Scale.
    public float scale = 1.0f;
    // The OpenGL ES texture handle to draw.
    private int currTexName;
    private TextureData texData;
    // The id of the original resource that the firstCurrTexName is based on.
    private int mResourceId;
    
    public Sprite() {
    }
    
    public Sprite(int resourceId, int type, int subType) {
        mResourceId = resourceId;
        this.type = type;
        this.subType = subType;
    }
    
    public void setResourceId(int id) {
        mResourceId = id;
    }
    
    public int getResourceId() {
        return mResourceId;
    }

	public void setCurrentTexture(TextureData texture) {
		this.cFrame = 0;
		this.texData = texture;
		this.currTexName = this.texData.mTextureName;
	}

	public TextureData getCurrentTexture() {
		return this.texData;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getWidth() {
		return width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getHeight() {
		return height;
	}
    
	public void setType(int type, int subType){
		this.type = type;
		this.subType = subType;
	}
	public void animate(){
		cFrame = (cFrame +1) % this.texData.nFrames;
	}

	public int getSubType() {
		return this.subType;
	}
	
	public boolean equals(Object sprite){
		if(Sprite.class.isInstance(sprite)){
			Sprite testSprite = (Sprite) sprite;
			if(testSprite.height == this.height && 
			   testSprite.width  == this.width  &&
			   testSprite.texData == this.texData){
				
				return true;
			}
			
			else
				return false;
		}
		else{
			return false;
		}
	}
}
