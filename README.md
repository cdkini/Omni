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
  // Methods
  abstract public String getType();
  
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
  private ArrayList<Tree> trees;
  private ArrayList<Blob> blobs;
  
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
Creates a new Omni version control system in the current directory by means of initializing a .omni directory. This system will automatically start with one commit: a commit that contains no files and has the commit message initial commit.
##### Usage: 
`omni init`
##### Complexity:
<b>O(1)</b> for the creation of the .omni directory
##### Failure Cases:
A .omni directory already exists in the current directory; an error stating <i>“Error: An Omni instance already exists in the current directory.”</i> will be raised.
##### Dangers:
N/A

## add
##### Description:
Adds a file or directory to the stage to be included in an upcoming commit. If the file had been marked for removal, it is unstaged.
##### Usage:
`omni add [file]`  
`omni add [dir]`
##### Complexity:
<b>O(N)</b> for the addition of a file or directory to the stage where N is the size of the added object
##### Failure Cases:
- If the file or directory does not exist, an error stating <i>"Error: Added file or directory not found."</i> will be raised.  
- If the file has not been modified since the last commit, an error stating <i>"Error: Staged file has not been modified since the last commit."</i> will be raised.
##### Dangers:
N/A

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
