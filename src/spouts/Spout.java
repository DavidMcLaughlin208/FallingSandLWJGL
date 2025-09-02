package spouts;

import matrix.CellularMatrix;
import matrix.CellularMatrix.FunctionInput;

import java.util.function.Consumer;

public interface Spout {

    FunctionInput setFunctionInputs(FunctionInput functionInput);

    Consumer<FunctionInput> getFunction();
}
