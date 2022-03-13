# ocr

[OCR (Optical Character Recognition)](https://en.wikipedia.org/wiki/Optical_character_recognition) engine adapted in Java.

- University project done in pairs by [@avirgos](https://github.com/avirgos) and [@EthanKrako](https://github.com/EthanKrako).

__Global objective :__
```
Build an OCR engine to recognise numbers (0 to 9) from a given image base (120 images, 100 of which are numbers).
```
______________________________________

# Processing chain

- __Resize__ of images to the same size -> easier handling
- __Binarization__ of images -> better readability and obtaining sharp edges

______________________________________

# Learning chain

\+ Isoperimetric report

\+ Zoning

\+ Vertical profile

______________________________________

# What is a confusion matrix ?

A confusion matrix is used to group together all the digit __recognition results__ : what was expected and what the OCR engine found.   
Thus, if the expected digit is the same as the recognised digit then the recognition worked.  
The number of __correct results__ can therefore be seen on the __diagonal__ shown below. 

![diagonal-confusion-matrix]()

______________________________________

# Final result

![final-confusion-matrix-with-result]()

After simplifying the images and applying our processing chain, we obtain a __recognition rate__ of __89%__ on 100 images corresponding to numbers from 0 to 9.

______________________________________

__Library :__

- __ImageJ__. [Download .jar](https://imagej.nih.gov/ij/download.html)
