import java.util.Random;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Compression {
	
	private static Mat codebook, T, test, encode, decode;
	
	
	public static String compression(String imageTest, String imgTraining, int refinement) {
		System.out.println("Initializing...");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		if(imgTraining == null) {
			codebookRandom();
		}
		else {
			codebookGeneration(imgTraining, refinement);
		}
		encoding(imageTest);
		return decoding(imageTest);
	}

	private static void codebookGeneration(String imgTraining, int refinement) {
		//Read training image
		Mat image = Imgcodecs.imread(imgTraining);
		if(image.empty()) {
			System.out.println("Training Matrix is empty!");
			System.exit(1);
		}
		//Convert training image to grayscale
		Mat training = new Mat(image.rows(), image.cols(), image.type());
		Imgproc.cvtColor(image, training, Imgproc.COLOR_RGB2GRAY);
		
		//Create random codebook with random values between 0 and 255
		codebook = new Mat(256, 9, CvType.CV_64FC1);
		for(int i = 0; i < codebook.rows(); i++) {
			for(int j = 0; j < codebook.cols(); j++) {
				codebook.put(i, j, new Random().nextDouble() * 256);
			}
		}


		System.out.println("Reading training image...");
		//Unsigned 8bits 0-255 CV_8UC1
		T = new Mat(7225, 9, codebook.type());
		int c = 0;
		for(int i = 0; i < training.rows() - 3; i += 3) {
			for(int j = 0; j < training.cols() - 3; j += 3) {
				T.put(c, 0, rowToArray(training.row(i    ).colRange(j, j + 3)));
				T.put(c, 3, rowToArray(training.row(i + 1).colRange(j, j + 3)));
				T.put(c, 6, rowToArray(training.row(i + 2).colRange(j, j + 3)));
				c++;
			}
		}
		
		Mat partition;
		Mat occurrences;
		
		System.out.println("Creating Partitions...");
		//Refinement process is iterated 6 times
		for(int i = 1; i <= refinement; i++) {
			System.out.println("Iteration number: " + i);
			
			partition = Mat.zeros(codebook.rows(), codebook.cols(), codebook.type());
			occurrences = Mat.zeros(codebook.rows(), 1, codebook.type());
			
			//For each row of T
			for(int j = 0; j < T.rows(); j++) {
				int R = assoc(T.row(j), codebook);
				Core.add(partition.row(R), T.row(R), partition.row(R));
				occurrences.put(R, 0, occurrences.get(R, 0)[0] + 1);
			}
					
			//For each occurrence NOT equal 0, divide partition by occurrences
			//We have a mediate version of training samples assigned to partition
			for(int j = 0; j < codebook.rows(); j++) {
				if(occurrences.get(j, 0)[0] != 0) {
					Core.divide(partition.row(j), occurrences.row(j), codebook.row(j));
				}
			}
		}
	}

	private static void codebookRandom() {
		codebook = new Mat(256, 9, CvType.CV_64FC1);
		for(int i = 0; i < codebook.rows(); i++) {
			for(int j = 0; j < codebook.cols(); j++) {
				codebook.put(i, j, new Random().nextDouble() * 256);
			}
		}
		T = new Mat(7225, 9, codebook.type());
	}
	
	private static void encoding(String imgTest) {
		//Read test image
		Mat image = Imgcodecs.imread(imgTest);
		if(image.empty()) {
			System.out.println("Test Matrix is empty!");
			System.exit(1);
		}
		//Convert test image to grayscale
		test = new Mat(image.rows(), image.cols(), image.type());
		Imgproc.cvtColor(image, test, Imgproc.COLOR_RGB2GRAY);
		

		System.out.println("Reading test image...");
		int c = 0;
		for(int i = 0; i < test.rows() - 3; i += 3) {
			for(int j = 0; j < test.cols() - 3; j += 3) {
				T.put(c, 0, rowToArray(test.row(i    ).colRange(j, j + 3)));
				T.put(c, 3, rowToArray(test.row(i + 1).colRange(j, j + 3)));
				T.put(c, 6, rowToArray(test.row(i + 2).colRange(j, j + 3)));
				c++;
			}
		}
		
		
		System.out.println("Code Association...");
		encode = new Mat(1, T.rows(), codebook.type());
		for(int i = 0; i < T.rows(); i++) {
			encode.put(0, i, assoc(T.row(i), codebook));
		}
	}

	
	private static String decoding(String imgTest) {
		System.out.println("Decoding...");
		
		decode = new Mat(test.rows() - 1, test.cols() - 1, codebook.type());
		
		int c = 0;
		for(int i = 0; i < test.rows() - 3; i += 3) {
			for(int j = 0; j < test.cols() - 3; j += 3) {
				decode.put(i    , j, rowToArray(codebook.row((int) encode.get(0, c)[0]).colRange(0, 3)));
				decode.put(i + 1, j, rowToArray(codebook.row((int) encode.get(0, c)[0]).colRange(3, 6)));
				decode.put(i + 2, j, rowToArray(codebook.row((int) encode.get(0, c)[0]).colRange(6, 9)));
				c++;
			}
		}
		decode.convertTo(decode, CvType.CV_8UC1);
		String newFile = imgTest.split("\\.")[0] + "Out.jpg";
		Imgcodecs.imwrite(newFile, decode);
		
		System.out.println("Done!");
		
		return newFile;
	}

	
	
	private static int assoc(Mat V, Mat codebook) {
		int index = 0;
		double errMin = 1000000000;

		for(int i = 0; i < codebook.rows(); i++) {
			Mat temp = new Mat(1, codebook.cols(), codebook.type());
			Core.subtract(V, codebook.row(i), temp);
			Core.pow(temp, 2, temp);
			
			double err = Core.sumElems(temp).val[0];
			if(err < errMin) {
				errMin = err;
				index = i;
			}
		}
		return index;
	}

	private static double[] rowToArray(Mat row) {
		double[] array = new double[row.cols()];
		for(int i = 0; i < row.cols(); i++) {
			array[i] = row.get(0, i)[0];
		}
		return array;
	}
}