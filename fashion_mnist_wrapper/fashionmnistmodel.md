# FashionMnistModel Usage

```
import com.forever.bee.tflite.task.wrapper.FashionMnistModel;

// 1. Initialize the Model
FashionMnistModel model = null;

try {
    model = FashionMnistModel.newInstance(context);  // android.content.Context
} catch (IOException e) {
    e.printStackTrace();
}

if (model != null) {

    // 2. Set the inputs
    // Prepare tensor "image" from a Bitmap with ARGB_8888 format.
    Bitmap bitmap = ...;
    TensorImage image = TensorImage.fromBitmap(bitmap);
    // Alternatively, load the input tensor "image" from pixel values.
    // Check out TensorImage documentation to load other image data structures.
    // int[] pixelValues = ...;
    // int[] shape = ...;
    // TensorImage image = new TensorImage();
    // image.load(pixelValues, shape);

    // 3. Run the model
    FashionMnistModel.Outputs outputs = model.process(image);

    // 4. Retrieve the results
    List<Category> probability = outputs.getProbabilityAsCategoryList();
}
```
