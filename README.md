# Omni Design Document

## Background
A lightweight, CLI-based version control system built in Java.  

# Classes and Data Structures
` Include here any class definitions. For each class list the instance variables  and static variables (if any). Include a brief description of each variable and its purpose in the class. Your explanations in this section should be as concise as possible. Leave the full explanation to the following sections. You  may cut this section short if you find your document is too wordy.`
## Repo
## OmniObject
```
/* Abstract class for primary Omni objects, including blobs, trees, and commits */
public abstract class OmniObject implements Serializable {
  // Fields
  
  // Methods
  public String getType()
  
}
```

## Blob
```
/* Stores the contents of a single file */
public class Blob extends OmniObject implements Serializable {
  // Fields  
  private byte[] contents;
  private String fileName;
  
  // Constructor  
  public Blob(File file);
  
  // Methods
  public String getName();
  
}
```

## Tree
```
/* Stores the contents of a directory. Includes pointers to blobs and other trees contained within */
public class Tree extends OmniObject implements Serializable {
  // Fields  
  private String dirName;
  private ArrayList<OmniObject> children;
  
  // Constructor  
  
  // Methods  
  
}
```

## Commit
```
/* Stores metadata about a user commit and points towards a tree to represent a snapshot of the repo */ 
public class Commit extends OmniObject implements Serializable {
  // Fields
  private Tree root;
  private Commit parent;
  private String author;
  private int timeStamp;
  private String message;
  
  // Constructor  
  
  // Methods  
  
}
```

## Branch
## Stage
## HEAD


# Algorithms
` This is where you tell us how your code works. For each class, include a high-level description of the methods in that class. That is, do not include a line-by -line breakdown of your code, but something you would write in a javadoc comment above a method, including any edge cases you are accounting for. We have read the project spec too, so make sure you do not repeat or rephrase what is stated there. This should be a description of how your code accomplishes what is stated in the spec.`

## init
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## add
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## commit
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## rm
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## log
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## global-log
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## find
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## status
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## checkout
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## branch
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## rm-branch
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## reset
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:

## merge
##### Description:
##### Usage:
##### Complexity:
##### Failure Cases:
##### Dangers:


# Persistence
` Describe your strategy for ensuring that you don’t lose the state of your program across multiple runs.`
