package input;

import input.processors.InputProcessor;
import matrix.CellularMatrix;
import input.processors.CreatorInputProcessor;
//import input.processors.PlayerInputProcessor;

public class InputProcessors {

    private final InputProcessor creatorInputProcessor;
//    private final InputProcessor playerInputProcessor;
    private InputProcessor activeProcessor;

    public InputProcessors(InputManager inputManager, CellularMatrix matrix, long window) {
//        this.playerInputProcessor = new PlayerInputProcessor(this, gameManager);
        this.creatorInputProcessor = new CreatorInputProcessor(this, inputManager, matrix, window);
        this.activeProcessor = this.creatorInputProcessor;
    }

//    public void setPlayerProcessor() {
//        Gdx.input.setInputProcessor(playerInputProcessor);
//    }

    public void setCreatorInputProcessor() {
        this.activeProcessor = this.creatorInputProcessor;
    }

    public void process() {
        this.activeProcessor.process();
    }

    public void renderUi() {
        this.activeProcessor.renderUi();
    }
}

