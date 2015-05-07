import os
import bpy


def makeTexture():
    sTex = bpy.data.textures.new('BumpTex', type = 'VORONOI')
    sTex.noise_scale = 0.2

def setMaterial(ob, mat):
    for m in ob.material_slots:
        m.material = mat
       
        

if __name__ == "__main__":
    full_path_to_directory = os.path.join('C:\\', 'Users', 'Samriddhi', 'Desktop', 'Blender', 'Obj-FaceHappy', 'Try')
 
    # get list of all files in directory
    file_list = os.listdir(full_path_to_directory)
 
    #reduce the list to files ending in 'obj'
    # using 'list comprehensions'
    obj_list = [item for item in file_list if item[-3:] == 'obj']
    i = 0
    # loop through the strings in obj_list.
    for item in obj_list:
        scn = bpy.context.scene
        scn.frame_start = 1
        scn.frame_end = 2
        scn.update()
        
        
        for obj in scn.objects:
          scn.objects.unlink(obj)
        
        bpy.ops.mesh.primitive_plane_add(location=(0,3,0),rotation=(1.04, 0, 1.5))
        for ob in scn.objects:
            ob.name = "Plane"
            
        mat1 = bpy.data.materials.new('Strand')
        mat1.texture_slots.add()
        mat1.specular_color = (1,1,1)
        mat1.specular_intensity = 1
        bpy.context.object.data.materials.append(mat1)
        
        
        sTex = bpy.data.textures.new('Texture.001',type='VORONOI')
        #sTex.noise_scale = 0.01 #med
        sTex.noise_scale = 0.05
        
        pTex = bpy.data.textures.new('Texture.002',type='VORONOI')
        #pTex.noise_scale = 0.002 #med
        pTex.noise_scale = 0.013 #med
        
        eTex = bpy.data.textures.new('Texture.003',type='VORONOI')
        eTex.noise_scale = .6 #large
        
        mTex = bpy.data.textures.new('Texture.004',type='VORONOI')
        mTex.noise_scale = 100 #large
        
        bpy.ops.transform.resize(value=(10,8,12))
        bpy.ops.transform.rotate(value=-0.9,axis=(0,0,1))
        
        full_path_to_file = os.path.join(full_path_to_directory, item)
        bpy.ops.import_scene.obj(filepath=full_path_to_file)
   
        j = 0
        bpy.ops.transform.rotate(value=0.9,axis=(0,0,1))
        for ob in scn.objects:
            me = ob.data
            for mat in me.materials: 
                mat.use_shadeless = True
                mat.texture_slots[0].texture = sTex
                mat.texture_slots[0].color = (0, 0, 0)
                mat.specular_color = (1,1,1)
                mat.diffuse_color = (1,1,1)
                if ob.name == "Plane":
                   mat.texture_slots[0].texture = pTex
                if "eye" in ob.name:
                   mat.texture_slots[0].texture = eTex   
                if ("tongue" or "teeth") in ob.name:
                   mat.texture_slots[0].texture = eTex
                if "sock" in ob.name:
                   mat.texture_slots[0].texture = mTex
                
        bpy.ops.transform.translate(value=(0.1, -0.93, -0.94))        
           
            # Camera
        cam_data = bpy.data.cameras.new("MyCam")
        cam_ob = bpy.data.objects.new(name="MyCam", object_data=cam_data)
        scn.objects.link(cam_ob)  # instance the camera object in the scene
        scn.camera = cam_ob       # set the active camera
        cam_ob.location = 5.5, -5.7, 2.2
        cam_ob.rotation_euler = 1.11, 0.011, 0.82
    
        # Lamp
        lamp_data = bpy.data.lamps.new("MyLamp", 'POINT')
        lamp_ob = bpy.data.objects.new(name="MyCam", object_data=lamp_data)
        scn.objects.link(lamp_ob)
        lamp_ob.location = 5.5, -5.7, 2.2
        lamp_ob.rotation_euler = .65, .055, 1.87       
        
        poss_name = os.path.join('C:\\', 'Users', 'Samriddhi', 'Desktop', 'blender', 'Output')
        save_name = poss_name+'//result'+str(i)+'.jpg'
      
        
        
        render = scn.render
        render.use_file_extension = True
        render.filepath = save_name
        bpy.ops.render.render(write_still=True)
        i += 1
        
        
    
