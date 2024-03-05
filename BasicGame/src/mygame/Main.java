package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;


public class Main extends SimpleApplication {

    // Nodos para representar el sol y los planetas en la escena
    public Node sun;
    public Node earth, moon, planet1, planet2, planet3, planet4;

   
    public static void main(String[] args) {
      
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(true);
        settings.setTitle("Sistema Solar");
        settings.setSettingsDialogImage("Interface/Solar.png");
       
        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    // Inicialización de la escena 3D
    @Override
    public void simpleInitApp() {
        // Creación de los planetas con sus atributos y colores
    sun = createPlanet("Sun", ColorRGBA.Yellow, 0f, 0f, 0.5f, 1.7f);
            /** Uses Texture from jme3-test-data library! */
    ParticleEmitter fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
    Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    //fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
    fireEffect.setMaterial(fireMat);
    fireEffect.setImagesX(2); fireEffect.setImagesY(2); // 2x2 texture animation
    fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );   // red
    fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
    fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
    fireEffect.setStartSize(0.6f);
    fireEffect.setEndSize(0.1f);
    fireEffect.setGravity(0f,0f,0f);
    fireEffect.setLowLife(0.5f);
    fireEffect.setHighLife(3f);
    fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
    rootNode.attachChild(fireEffect);

        earth = createPlanet("Earth", ColorRGBA.Blue, 5f, 1.0f, 0.5f, 1f);
        moon = createPlanet("Moon", ColorRGBA.Gray, 1.5f, 3.0f, 1.5f, 0.5f); 
        planet1 = createPlanet("Planet1", ColorRGBA.Red, 8f, 1.3f, 0.8f, 1.2f);
        planet2 = createPlanet("Planet2", ColorRGBA.Green, 11f, 1.8f, 1.2f, 1.3f);
        planet3 = createPlanet("Planet3", ColorRGBA.Orange, 14f, 2.2f, 1.5f, 1.4f);
        planet4 = createPlanet("Planet4", ColorRGBA.Cyan, 17f, 2.7f, 2.0f, 1.5f);

        // Adjuntar el sol y los planetas a la raíz de la escena
        rootNode.attachChild(sun);

        // Crear un nodo para agrupar los planetas y adjuntarlo a la raíz
        Node planetsNode = new Node("PlanetsNode");
        planetsNode.attachChild(earth);
        earth.attachChild(moon);
        planetsNode.attachChild(planet1);
        planetsNode.attachChild(planet2);
        planetsNode.attachChild(planet3);
        planetsNode.attachChild(planet4);

        rootNode.attachChild(planetsNode);
    }

    // Actualización de la escena en cada frame
    @Override
    public void simpleUpdate(float tpf) {
        // Rotar todo el sistema solar alrededor del eje Y
        rootNode.rotate(0, tpf * 0.2f, 0);

        // Actualizar posiciones y rotaciones de los planetas
        updatePlanet(earth, tpf, 1.0f, 0.5f);
        updatePlanet(moon, tpf, 3.0f, 1.5f);
        updatePlanet(planet1, tpf, 0.8f, 1.0f);
        updatePlanet(planet2, tpf, 1.2f, 1.2f);
        updatePlanet(planet3, tpf, 1.5f, 1.8f);
        updatePlanet(planet4, tpf, 2.0f, 1.5f);
    }

    // Método para crear un planeta con su forma, material y atributos iniciales
    private Node createPlanet(String name, ColorRGBA color, float distance, float orbitSpeed, float rotationSpeed, float scale) {
        // Crear geometría para representar el planeta
        Box planetShape = new Box(0.5f * scale, 0.5f * scale, 0.5f * scale);
        Geometry planetGeom = new Geometry(name, planetShape);

        // Aplicar color al material del planeta
        Material planetMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        planetMaterial.setColor("Color", color);
        planetGeom.setMaterial(planetMaterial);

        // Crear un nodo para representar el planeta y adjuntar la geometría
        Node planet = new Node(name);
        planet.attachChild(planetGeom);

        // Establecer la posición inicial del planeta en su órbita
        planet.setLocalTranslation(distance, 0, 0);

        // Establecer la rotación inicial del planeta
        planet.rotate(0, FastMath.nextRandomFloat() * FastMath.PI, 0);

        // Establecer la velocidad de órbita como datos de usuario del nodo
         planet.setUserData("OrbitSpeed", orbitSpeed / distance);  // Ajusta el factor para los planetas más lejanos

        return planet;
    }

    // Método para actualizar la posición y rotación de un planeta en su órbita
    private void updatePlanet(Node planet, float tpf, float rotationSpeed, float orbitSpeed) {
        // Rotar el planeta alrededor de su propio eje
        planet.rotate(0, tpf * rotationSpeed, 0);

        // Mover el planeta en su órbita circular
        float orbitDistance = planet.getLocalTranslation().length();
        float currentAngle = FastMath.atan2(planet.getLocalTranslation().z, planet.getLocalTranslation().x);
        float newAngle = currentAngle + tpf * orbitSpeed;

        float newX = orbitDistance * FastMath.cos(newAngle);
        float newZ = orbitDistance * FastMath.sin(newAngle);

        planet.setLocalTranslation(newX, 0, newZ);
    }

    // Método para renderizar la escena (no se necesita código de renderizado para este ejemplo)
    @Override
    public void simpleRender(RenderManager rm) {
        // No se necesita código de renderizado para este ejemplo
    }
}
