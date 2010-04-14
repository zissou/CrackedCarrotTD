#include "render.h"
#define LOG_TAG "NATIVE_RENDER"

void Java_com_crackedcarrot_NativeRender_nativeResize(JNIEnv*  env, jobject  thiz, jint w, jint h){
	
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_SURFACE_RESIZE", "The surface has been resized!.");
	
	glViewport(0, 0, w, h);
	
	/*
	 * Set our projection matrix. This doesn't have to be done each time we
	 * draw, but usually a new projection needs to be set when the viewport
	 * is resized.
	 */
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrthof(0.0f, w, 0.0f, h, 0.0f, 1.0f);	
	
	glShadeModel(GL_FLAT);

	glEnable(GL_BLEND);
	glEnable(GL_TEXTURE_2D);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);	
	
	/*
	 * By default, OpenGL enables features that improve quality but reduce
	 * performance. One might want to tweak that especially on software
	 * renderer.
	 */
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_DITHER);
	glDisable(GL_LIGHTING);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	
	glClearColor(0, 0, 0, 1);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_MODELVIEW);
	
}

void Java_com_crackedcarrot_NativeRender_nativeDrawFrame(JNIEnv*  env){
	
    int i,j;

	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	for(j = 0; j < 6; j++){
		for (i = 0; i < noOfSprites[j]; i++) {
			if((*env)->GetBooleanField(env,renderSprites[j][i].object, renderSprites[j][i].draw)){
    				drawSprite(env, &renderSprites[j][i]);
			}
	    }
	}
	glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}

void drawSprite(JNIEnv* env, GLSprite* sprite){

    GLuint* bufferName;
	GLuint* texBufNames;
	int nFrames;
    GLint currTexture = -1;
    GLint prevTexture = -2;
	GLfloat r, g, b, a;
	GLfloat scale;

    bufferName = sprite->bufferName;
	texBufNames = sprite->textureBufferNames;
		
	r = (*env)->GetFloatField(env, sprite->object, sprite->r);
	g = (*env)->GetFloatField(env, sprite->object, sprite->g);
	b = (*env)->GetFloatField(env, sprite->object, sprite->b);
	a = (*env)->GetFloatField(env, sprite->object, sprite->opacity);
			
	scale = (*env)->GetFloatField(env, sprite->object, sprite->scale);
	nFrames = (*env)->GetIntField(env, sprite->object, sprite->nFrames);
	
	currTexture = (*env)->GetIntField(env,sprite->object, sprite->textureName);
	if(currTexture == 0){
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "EEEK! INVALID TEXTUREID BAD ! BAD! %d", currTexture);
	}
			
	if(currTexture != prevTexture){ 
    	glBindTexture(GL_TEXTURE_2D, currTexture);
		prevTexture = currTexture;
	}
	
	glPushMatrix();
	glLoadIdentity();
			
	glColor4f(r, g, b, a);
	glScalef(scale,scale,1);
	glTranslatef((*env)->GetFloatField(env, sprite->object, sprite->x),
				(*env)->GetFloatField(env, sprite->object, sprite->y), 0);
		
	glBindBuffer(GL_ARRAY_BUFFER, bufferName[VERT_OBJECT]);
	glVertexPointer(3, GL_FLOAT, 0, 0);
	
	if(nFrames > 1){
		glBindBuffer(GL_ARRAY_BUFFER, texBufNames[(*env)->GetIntField(env, sprite->object, sprite->cFrame)]);
		/*__android_log_print(ANDROID_LOG_DEBUG, 
                            LOG_TAG, 
                            "Setting TextureBuffer %d",
                            texBufNames[nFrames]);*/
       __android_log_print(ANDROID_LOG_DEBUG, 
                           LOG_TAG, 
                           "This should not happen, yet");
	}
	else{
	
		glBindBuffer(GL_ARRAY_BUFFER, texBufNames[0]);
		/*__android_log_print(ANDROID_LOG_DEBUG, 
                            LOG_TAG, 
                            "Setting TextureBuffer %d",
                            texBufNames[0]);*/
        /*__android_log_print(ANDROID_LOG_DEBUG, 
                            LOG_TAG, 
                            "Only one frame on this sprite, use frame no 0");*/
	}
	glTexCoordPointer(2, GL_FLOAT, 0, 0);
		
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName[INDEX_OBJECT]);
    /*__android_log_print(ANDROID_LOG_DEBUG, 
                          LOG_TAG, 
                          "Drawing Sprite: (%d, %d) From vertBuffer: %d  IndexBuffer %d and texBuf %d" ,
                           j,i, bufferName[VERT_OBJECT], bufferName[INDEX_OBJECT], texBufNames[0]);*/
	glDrawElements(GL_TRIANGLES, sprite->indexCount, GL_UNSIGNED_SHORT, 0);
				
    //__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "GL_ERROR: %d", glGetError());
				
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			
	glPopMatrix();
	
}

void Java_com_crackedcarrot_NativeRender_nativeSurfaceCreated(JNIEnv*  env){
	__android_log_print(ANDROID_LOG_DEBUG, "NATIVE_SURFACE_CREATED", "The surface has been created.");
	
}
