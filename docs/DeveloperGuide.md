---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# LookMeUp Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

LookMeUp  is a brownfield software project based off AddressBook Level-3, taken under the CS2103T Software Engineering,
at National University of Singapore.

1. The UI features of `AddCommandHelper` was reused with minimal changes from [Snom](https://github.com/RunjiaChen/ip).
2. `Fuzzy Input` was adapted from [geeksforgeeks](https://www.geeksforgeeks.org/bk-tree-introduction-implementation/).
3. GitHub Co-Pilot was used sparingly as an autocomplete tool in the writing of some code snippets.


--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `remove 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

The diagram below represents a partial implementation of the UI components of LookMeUp

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `CommandHelperWindow` has a different implementation from the rest of the UI components, which will be explained in detail at the Add By Step feature. This is to avoid cluttering the architecture diagram.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("remove 1")` API call as an example.

<puml src="diagrams/RemoveSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `remove 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `RemoveCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `RemoveCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `RemoveCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to remove a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `RemoveCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

<puml src="diagrams/AddByStepClass.puml" width="400"/>

How the AddCommandHelper works:
* When the user enters an input into the CommandHelperWindow, AddCommandHelper will use the methods defined in `ParserUtil` to check whether the input by the user is valid. This will be explained in detail in the AddByStep Feature. 

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Undo/redo feature

#### Implementation

The undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, 
stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following 
operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and 
`Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

* Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the 
initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

* Step 2. The user executes `remove 5` command to remove the 5th person in the address book followed by a `yes` 
confirmation. The confirmation command calls `Model#commitAddressBook()`, causing the modified state of the address book 
after the removal execution to be saved in the `addressBookStateList`, and the `currentStatePointer` is 
shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

* Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls 
`Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state 
will not be saved into the `addressBookStateList`.

</box>

* Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the 
`undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once 
to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no 
previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the 
case. If so, it will return an error to the user rather than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the 
lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` 
once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address 
book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` 
to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

* Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as 
`list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. 
Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

* Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not 
pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be 
purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern 
desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `remove`, just save the person being removed).
  * Cons: Need to ensure that the implementation of each individual command are correct.

### Safe-Removal feature

#### Implementation

The feature to remove contacts from the address book is facilitated by `RemoveCommand` and `RemoveConfirmation`.

The safe-removal mechanism consists of several components:
1. `RemoveCommand`: A class that takes in the `Index` of a contact in the list, and "spotlights" this contact through 
  `Model#getFilteredPersonList()` to then prompt the user to confirm the removal of the target person. This class 
   does not perform the actual removal of the contact.
2. `RemoveCommandParser`: A class that parses the user input to determine the target person to be removed. The class 
   parses the `Index` input when users key in `remove INDEX`, to proceed with the confirmation process of the actual 
   contact to be removed.


Below is the sequence diagram outlining the execution of `RemoveCommand`.

<puml src="diagrams/RemoveSequenceDiagram.puml" alt="RemoveSequenceDiagram" />

<box type="info" seamless>

**Note:** The lifeline for `RemoveCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the
lifeline reaches the end of diagram.

</box>

3. `RemoveConfirmation`, `RemoveSuccess` and `RemoveAbortion`: Classes that prompt the user to confirm the removal of
   the target person, performing the actual deletion of the contact (or abortion of process), then providing feedback on
   the success or failure of the removal process.


Below is the sequence diagram outlining the execution of `RemoveSuccess`, where the user confirms the removal of a contact.

<puml src="diagrams/RemoveConfirmationSequenceDiagram.puml" alt="RemoveConfirmationSequenceDiagram" />


Our implementation follows Liskov's Substitution Principle closely. `RemoveConfirmation` was designed to be an abstract
class to allow for extension of the 2 confirmation methods via the `RemoveSuccess` and `RemoveAbortion` classes. This
decision makes it easier to group similar methods and messages together for better code extendability and
maintainability when it comes to enhancing the confirmation process.

<puml src="diagrams/RemoveConfirmationClassDiagram.puml" alt="RemoveConfirmationClassDiagram" />

Given below is an example usage scenario and how the safe-removal mechanism behaves at each step.
> Assuming existing contacts in the address book (shown in a simplified list for ease of understanding):
> 1. Paul Walker
> 2. Alice Cooper
> 3. Dylan Walker
> 4. Paul Cooper


* Step 1: The user executes `remove 4` command.
    * The `remove` command calls `RemoveCommandParser#parseCommand()`, causing `RemoveCommand#execute()` to get called
    * `RemoveCommand` will proceed with the confirmation process of the actual contact to be removed.
        * The input will be parsed by `RemoveCommandParser` to obtain the intended `Index` to be removed.
        * User will then be prompted to confirm the removal of the contact with "yes"/"no"
        * The user will then key in `yes` or `no` to confirm or abort the removal process.
  > Are you sure you want to remove the following contact? (yes/no):
    > 1. Paul Walker

  
* Step 2a: The user confirms the removal of the contact by executing `yes` command.
    * The `yes` command calls `RemoveSuccess#execute()` to confirm the removal process.
    * The confirmation process will be handled by `RemoveSuccess` and its parent class `RemoveConfirmation`.
        * `RemoveSuccess#execute()` checks if the `yes` input is valid, calling `RemoveConfirmation#isValidInput()`
        * `RemoveConfirmation#isValidInput()` returns `true` if the input is valid, and `false` otherwise.
            * Validity of input is determined by the previous command executed by the user - a valid `remove INDEX`
          command, that serves as a precursor to the removal  confirmation process.
    * If the user confirms the removal with `yes`, `RemoveSuccess` will proceed with the removal process.
        * The contact will be removed from the address book and `RemoveSuccess` will provide feedback on the success of 
      the removal process.


* Step 2b: The user aborts the removal of the contact by executing `no` command.
    * The `no` command calls `RemoveAbortion#execute()` to abort the removal process.
    * The abortion process will be handled by `RemoveAbortion` and its parent class `RemoveConfirmation`.
        * `RemoveAbortion#execute()` checks if the `no` input is valid, calling `RemoveConfirmation#isValidInput()`
        * `RemoveConfirmation#isValidInput()` will return `true` if the input is valid, and `false` otherwise.
            * Validity of input is determined by the previous command executed by the user - a valid `remove INDEX`
          command, that serves as a precursor to the removal abortion process.
    * If the user aborts the removal with `no`, `RemoveCommand` will abort the removal process.
        * The default list of contacts will be shown with the text input of the `CommandBox` cleared, and `RemoveAbortion`
      will provide feedback on the abortion of the removal process.


* Step 2c: The user enters an invalid command e.g. `abc` instead of `yes`/`no` after the `remove 4` command.
    * The user will be prompted with an error message:  
    > Unknown Command
    * Since the current GUI remains with the spotlighted contact "Paul Walker", users will need to type `remove 1`
    to be prompted with the confirmation process again, or type `list` to return to the default list

Here is an activity diagram that summarizes the process of removing a contact from the address book:

<puml src="diagrams/SafeRemovalActivityDiagram.puml" alt="SafeRemovalActivityDiagram" />

<box type="info" seamless>

**Note:** The path from the guard condition `[User executes "remove 1"]` is supposed to point to `Parse index` action,
but due to the constraints of PlantUML, it has been simplified to point directly to the merge node below.

</box>

#### Design considerations:

Several design considerations were taken into account when implementing the safe-removal feature.

**Aspect 1**: Mechanism to perform the actual deletion upon confirmation_

Given the key purpose of this feature is for **SAFE** deletion, this step is crucial to ensure that there is a safety
net for users before the actual removal of the contact.


* **Alternative 1 (current choice)**: To prompt the users for confirmation via a `yes`/`no`, then proceed with
  parsing the `yes`/`no` user input as independent commands in `AddressBookParser`
  * Pros: Maintains a similar command structure/workflow as all other commands (to go through `AddressBookParser`)
  * Cons: Requires backward reference of previous command to check if the input was a valid `remove INDEX` input,
    currently implemented in `RemoveConfirmation#isValidInput()`, leading to a more complex implementation.


* **Alternative 2**: To create functions that directly handle the confirmation process within `RemoveCommand`
  * Pros: Removes the need to access the previous command and assess its validity
  * Cons: Changes the command structure/workflow
    * This alternative would require a more complex implementation, as the confirmation process would be directly
      handled within `RemoveCommand`, leading to a more monolithic class structure. This would make it harder to
      maintain and extend the code in the future, as the class would be responsible for the confirmation processes 
      **AND** the actual process of removing the contact, violating the Single Responsibility Principle.

**Decision**:

Weighing the pros and cons of Alternatives 1 and 2, we have decided to go with **Alternative 1**

Addressing the cons of Alternative 1, our current implementation is such that details of previous command are retrieved
from `RemoveCommandParser` within the `RemoveConfirmation#isValidInput()` method. This avoids exposure of the
`remove INDEX` command details, ensuring Separation of Concerns and adhering to the Single Responsibility Principle as
the `RemoveConfirmation` class solely handles the confirmation process itself and checks directly related to it.<br><br>


**Aspect 2**: For enhancing the removal process by first **shortlisting the contact to be removed** before 
proceeding with `remove INDEX`, potentially reducing the amount of scrolling to find the contact to be removed.

* **Alternative 1**: To use the same command word (i.e. `remove` - `remove NAME` then `remove INDEX`)
to perform the shortlisting of contacts with matching names, then identifying the specific contact to be removed by its 
index.
  * Pros: More intuitive for users to approach removal process
    * Using the same command word `remove` for both shortlisting and confirmation processes reduces the cognitive load,
    allowing the process to be more user-friendly
  * Cons: More complex execution process
    * This leads to ambiguity in the command execution process to new developers who are used to the conventions set
    by other commands, as this command structure would make use of an **overloaded** `RemoveCommand` constructor. 
    * This also introduces unnecessary complexity since the `RemoveCommandParser` will have to handle cases where 
    contact names contain numbers, and users seek to shortlist contacts with the numbers in the name, to avoid invoking 
    `remove INDEX` instead. 


* **Alternative 2 (current choice)**: To encourage users to use the existing `find` command to shortlist the contact(s) 
to be removed, then use the `remove INDEX` to identify the contact from a shorter list, proceeding with safe-removal.
  * Pros: Separates the shortlisting and confirmation processes to two distinct commands
    * This reduces ambiguity in the command execution process for future developers
  * Cons: Require 2 different commands for deletion, which may come as a slight inconvenience to users.

**Decision**:

Weighing the pros and cons of Alternatives 1 and 2, we have decided to go with **Alternative 2** due to the clarity of 
separation between the shortlisting and confirmation processes. Since this workflow is simply an enhancement to the 
removal process, and given how `find` is relatively intuitive to use, we believe that the maintaining the separation of 
shortlisting and removal using the existing `find` command would ultimately provide a more straightforward and intuitive 
experience to users.

**Other considerations**:

* **Separation of Concerns Principle**: Maintaining the separation of the shortlisting and contact removal confirmation 
processes (as opposed to overloading the `RemoveCommand` constructor) ensures that the command structure is clear 
and intuitive for future developer. This design decision promotes better code maintainability and extensibility, 
as the shortlisting process can be easily modified without affecting the confirmation process, especially since they
are separate concerns to begin with. By adhering to the Separation of Concerns Principle, it has also ensured that 
the `RemoveCommand` class adheres to the Single Responsibility Principle, as it is solely responsible for the 
confirmation process of the contact to be removed. <br><br>

  
### Fuzzy Input

#### Implementation

The BK-Tree data structure was employed by the implementation of the fuzzy input to effectively find words that are
close to the target word in terms of their Levenshtein distance. Each node in the tree-like data structure represents a
word and its children represent words that are one edit distance away. 

The fuzzy input implementation consists of several components:
<puml src="diagrams/FuzzyInputClassDiagram.puml" alt="FuzzyInputClassDiagram" />

<br>

1. `BkTreeCommandMatcher`: The main BK-Tree data structure for sorting and efficiently search for similar elements
2. `BkTreeNode`: Internal node structure used by the Bk-Tree
3. `FuzzyCommandParser`: A class demonstrating the usage of BK-tree for command parsing
4. `LevenshteinDistance`: An implementation of the DistanceFunction interface using the Levenshtein distance algorithm
<br>
<puml src="diagrams/FuzzyInputObjectDiagram.puml" alt="FuzzyInputObjectDiagram" />

Our implementation follows the SOLID principle closely. We have designed interfaces to promote flexibility, especially
complying with the Open-Close Principle. This design decision makes it easy to extend various `CommandMatchers` or
`DistanceFunctions` in the future, making it easier to incorporate alternative algorithms if need be.

Given below is an example usage scenario and how the fuzzy input mechanism behaves:

* Step 1 : User misspelled listing command `lust` instead of `list`. 
  * The `lust` command calls `FuzzyCommandParser#parseCommand()`, causing `BkTreeCommandMatcher#findClosestMatch()` to
  get called in response.
  * The `BkTree` would be already initialised with the list of commands before the call.
    * During the initialisation, `BkTree` calculates the distances between the commands and constructs the tree accordingly.
  * When `findClosestMatch()` is called, it initiates a search within the `BkTree` constructed.
    * Starting from root node, Bk-Tree traverses through nodes based on the distance between the target command `lust` 
    and commands stored in each `BkTreeNode`.
    * The closest match found based on the specified distance metric (1 misspell) will be returned, in this case `list`
    and `AddressBookParser#parseCommand()` will proceed on to the `list command`.
  * When calculating the distance between 2 commands, `BkTree` calls `DistanceFunction#calculateDistance()` method.
    * In this case, LevenshteinDistance class will calculate the distance.

* Step 2 : User entered unsupported command `peek`
    * The `peek` command calls `FuzzyCommandParser#parseCommand()`, causing `BkTreeCommandMatcher#findClosestMatch()` to
      get called in response.
    * Initialisation works the same as Step 1
    * `findClosestMatch()` does the same operation as Step 1
      * However, based on the Levenshtein Distance algorithm, the distance between `peek` and any commands stored in
      `BkTreeNode` will be greater than 1 which is greater than the specified distance metric.
      * `FuzzyCommandParser#parseCommand()` will return `null` string to `AddressBookParser#parseCommand()`
      * Since `null` is not a recognised command, `ParseException` will be thrown.

    
#### Design considerations:

[Common fuzzy search algorithm for approximate string matching](https://www.baeldung.com/cs/fuzzy-search-algorithm) 
were compared to determine the optimal algorithm for our AddressBook. 

* **Alternative 1 (current choice)** Bk-Tree with Levenshtein Distance Algorithm 
* Pros: Tree-like data structure
  * The hierarchical structure of BK-Tree allows search operations to run in logarithmic time,
  making them scalable for large datasets
  * BK-Tree can work with different types of data, not limited to strings
* Cons: Require more memory, a concern for memory-constrained environment

* **Alternative 2** Hamming Distance
  * Pros: Straightforward to calculate and understand
  * Cons: Only designed for comparing strings of equal length

* **Alternative 3** Bitap Algorithm
  * Pros: Efficient for finding approximate matches of given pattern within a text
  * Cons: Primarily designed for substring matching within texts

* **Alternative 4** Brute Force Method
  * Pros: Easily to implement, no pre-processing required, takes no extra space
  * Cons: Horrible run-time

For our AddressBook implementation, the `BK-Tree with Levenshtein Distance Algorithm` proved to be the optimal choice.
Its potential to extend code and efficiently handle misspelled or similar commands outweighs its memory usage and complexity of implementation. 
This algorithm guarantees fast runtime performance and robustness in command parsing.

### Sort feature

#### Implementation

The sorting mechanism is facilitated by `SortCommand`. It implements the following operations:
* `SortCommand#`: Constructor class which is instantiated and stores the necessary `SortStrategy` based on user input.
* `SortCommand#Executes`: Executes the necessary `SortStrategy` and update the model. 

The sorting mechanism consists of several components:
1. `SortStrategy`: An interface that requires implementations to define methods for sorting the address book and getting
the category associated with the sorting strategy.
2. `SortByTag` and `SortByName`: These classes implement `SortStrategy` interface to provide the specific strategies
of the AddressBook based on tags and names respectively. 
3. `SortCommand`: Initiates the sorting by parsing user input to determine the sorting criteria and calls the appropriate
sorting class based on the input. After sorting, it then updates the list of persons in the model. 

Given below is an example usage scenario and how the sorting mechanism behaves at each step.

* Step 1: The user launches the application for the first time, no contacts will be present in the `AddressBook`.
When user `add` contacts in the `AddressBook`, contacts will be sorted based on their timestamp.

* Step 2: The user executes `sort name` command.
  * The `sortCommand#` constructor will initialise with the `sortByName` strategy stored as `SortStrategy`.
  * `sortCommand#execute` will pass the current model's `AddressBook` to `sortStrategy#sort`, where `UniquePersonsList` 
  will be obtained and sorted lexicographically by name 
  * After sorting, the model will be updated to reflect the newly sorted contacts list, alongside a return statement
  to provide confirmation to the user.

    <puml src="diagrams/SortCommandSequenceDiagram.puml" alt="SortCommandSequenceDiagram" />
    
* Step 3: The user executes `sort tag` command.
    * The `sortCommand#` constructor will initialise with the `sortByTag` strategy stored as `SortStrategy`.
    * `sortCommand#execute` will pass the current model's `AddressBook` to `sortStrategy#sort`, where `UniquePersonsList`
      will be obtained and sorted lexicographically by tags
    * After sorting, the model will be updated to reflect the newly sorted contacts list, alongside a return statement
      to provide confirmation to the user.

* Step 4: The user executes `sort` command.
  * The `sortCommand#` constructor will first verify the presence of `condition input` before proceeding with 
  initialisation.
  * Since there is no condition stated, a `ParseException` will be thrown and a statement will be displayed to provide 
  the correct input and conditions to be stated.

#### Design consideration:
`SolidStrategy` interface was implemented for sorting functionality to adhere to SOLID principles, particularly the
Single Responsibility Principle, Interface Segregation Principle and Open/Close Principle.
* Single Responsibility Principle
  * The interface maintains single responsibility by defining methods for sorting strategies without burdening
  implementations with unrelated methods
* Open/Closed Principle
  * The interface provides an abstraction that allows for extension. New sorting strategies can be introduced by
  implementing `SortStrategy` interface without altering existing code.
* Interface Segregation Principle
  * Segregates behavior for sorting into distinct methods `sort` and `getCategory`, thus, allowing different sorting
  strategies to implement only the methods they need, rather than being forced to implement monolithic interface with
  unnecessary methods.
<br/>
* **Alternative 1 (current choice)** `sort` method of the `SortStrategy` to take in `AddressBook` as its parameter.
  * Pros: Straightforward design and easy to implement.
    * Sorting logic interacts directly with data structure being sorted.
  * Cons: May be challenging to apply sorting strategies to different data structures without modification.

* **Alternative 2** `sort` method of the `SortStrategy` to take in `model` as its parameter.
  * Pros: Sorting strategies can be applied to different data structures without modification
    * Promoting code reuse and scalability.
  * Cons: Requires access to `AddressBook` eventually, introducing unnecessary complexity.

Alternative 1 is chosen for the following reasons:
* Simplicity: keeps sorting logic simple and focused by directly interacting with the data structure being sorted.
* Clear Responsibility: Sorting logic is closely tied to the data structure it operates on, adhering to the Single
Responsibility Principle.
* Ease of implementation: No need to pass unnecessary parameters to the sorting method.
  * Reduce complexity and potential dependencies.
  * Clear outline has been established that the only data structure present is the `AddressBook` containing
  `UniquePersonList`.
    * There is not a need to apply sorting strategies to another different data structure.


### Add By Step

#### Overview
`addbystep` loads up a separate window, which will prompt the users for the necessary input fields for an `add` command.
When all the fields have been successfully entered by the user, the user can copy the formatted command to their 
clipboard.

#### Implementation

The architecture diagram given below explains the implementation for the UI for CommandHelperWindow

<puml src="diagrams/CommandHelperWindowClassDiagram.puml" alt="CommandHelperWindowClassDiagram" />

The functionality and purpose of the UI remains unchanged even though the implementation used for the UI is different. 




The `addbystep` feature is facilitated by the `AddCommandHelper` and the `CommandHelperWindow` class. The 
`CommandHelperWindow` serves as the UI for the user to interact with the `AddCommandHelper`. The `AddCommandHelper` is 
responsible for accepting and checking whether the user's input is valid or not before prompting the user for the next
input field. 



Given below is an example usage scenario and how the `AddCommandHelper` class behaves at each step. Note that while each 
step for accepting fields may come off as repetitive, the type of invalid inputs for each field is different. Thus, we 
wish to illustrate examples of invalid inputs for each field.

* Step 1: The user enters the `addbystep` command, causing the `CommandHelperWindow` to load up. It prompts the user for 
the name of the new contact.

* Step 2: The user enters the name of the new contact.
    * If the name entered by the user is invalid (i.e. not alphanumeric), an error message will be shown and the user
will have to enter the name again 
    * If the name entered by the user is valid, the user will be prompted to enter the next field (number)

* Step 3: The user enters the number of the new contact.
    * If the number entered by the user is invalid (i.e. one digit), an error message will be shown and the user will
will have to enter the number again
    * If the number entered by the user is valid, the user will be prompted to enter the next field (email) 

* Step 4: The user enters the email of the new contact.
    * If the email entered by the user is invalid (i.e. does not have the `@` symbol) an error message will be shown 
and the user will have to enter the email again
    * If the email entered by the user is valid, the user will be prompted to enter the next field (address)

* Step 5: The user enters the address of the new contact.
    * If the address entered by the user is invalid (i.e. blank), an error message will be shown and the user will have 
to enter the address again
    * If the address entered by the user is valid, the user will be prompted to type the copy command (`cp`)

From steps 2 - 5, attached below is an activity diagram of how the user interacts with the `AddCommandHelper` when they 
are keying in the necessary inputs. The `AddCommandHelper` continuously validates the user's input to ensure that they
have entered all the necessary fields correctly.

<puml src="diagrams/ProcessUserInputActivityDiagram.puml" alt="ProcessUserInputActivityDiagram" />


* Step 6: The user enters the `cp` command.
    * The user can enter anything at this stage, but only the `cp` command will result in the formatted `add` command 
to be copied to the clipboard. Other inputs will result in the same prompt message at the end of Step 5

* Step 7: The successfully copied message will be displayed to the user, and the user can now close the
`CommandHelperWindow` window.
    * The user can still continue interacting with the `CommandHelperWindow`, but those interactions are
meaningless, thus we will not go into the details of those interactions. 


Below is an activity diagram that summarizes the process of a user using the `addbystep` feature. 

<puml src="diagrams/AddByStepActivityDiagram.puml" alt="AddByStepActivityDiagram" />

#### Design considerations:

Aspect: How to implement assistance functions to aid users in typing their commands.

* **Alternative 1 (current choice)** Create a new helper class and GUI to prompt users for the necessary details.
  * Pros:
    * It is easy to implement a new class, and due to the high cohesion of the previous code, we are able to reuse
      methods defined previously in `ParserUtil`class to check the validity of the fields entered by the user
    * The `CommandHelper` class can be implemented separately from the rest of the classes. This results in lower coupling
      between the newly implemented `CommandHelper` class and the remaining classes, resulting in easier maintenance and
      integration
  * Cons:
      * The startup of another GUI for the helper class may introduce lag, especially on the older computers

* **Alternative 2** Implement a command to display the format for users to follow.
  * Pros:
    * It easier to implement as compared to the `CommandHelper` class, since prompts do not actually have any form of user
    interaction
  * Cons:
      * It does not benefit users as much, as they can still make mistakes when it comes to following the exact format
    of the command

* **Alternative 3** Implement a function to autocomplete commands for users.
  * Pros:
      * It can be built directly into the original GUI for AddressBook, there is no need for a separate GUI for the
      `CommandHelper` class
  * Cons:
      * Autocomplete is only able to fill in certain parts of the command for the user (i.e. the prefixes for names, 
      tags). It cannot fill in the exact details 
      * It is more difficult to implement as the users may try to autocomplete an invalid command, so there may be a need 
      to perform checking of the command first, before letting the user know that the entered command is invalid.

### Duplicate feature

#### Implementation

The feature to be able to add persons with duplicate names in the address book are facilitated by the use of the
`DuplicateCommand`. It implements the following operations:
* `DuplicateCommand#`: Constructor class which is instantiated and stores the necessary `toAdd` person object
    based on user input.
* `DuplicateCommand#Executes`: Executes the necessary `addDuplicatePerson` method and updates the model.

The sorting mechanism consists of several components:
1. `addDuplicatePerson`: A method bound by the `Person`, `ModelManager`, `AddressBook` classes that each contain
    similar logic to support a SLAP form of implementation for the end execution point i.e. `execute` in 
    `DuplicateCommand`.
2. `DuplicateCommand`: Initiates the duplication by parsing user input to determine the identity of the person to add. 
    After duplicating, it then updates the list of persons in the model.

Given below is an example usage scenario and how the feature mechanism behaves at each step.

* Step 1: The user launches the application for the first time, no contacts will be present in the `AddressBook`.
  When user `add` contacts in the `AddressBook`, contacts will be sorted based on their timestamp.

* Step 2: The user reaches a point where they encounter the need to have to add a separate contact, that has the exact
  same name as another person in their `AddressBook`.

* Step 3: To continue, the user executes `add /n... /e ...` to attempt to add this new person.

* Step 4: The user then receives an error in their `AddressBook` which alerts them that they already have such a person
  in their `AddressBook`, and they have the option of creating a duplicate of this contact.

* Step 5: The user picks their choice and edits the command in their current `CommandBox`, replacing `add` with 
  `duplicate`, leaving the rest of the arguments untouched.

* Step 6: The user executes `duplicate /n... /e...` command.
    * The `DuplicateCommand#` constructor will initialize with the `toAdd` variable based on the created `Person` 
        object in `DuplicateCommandParser`.
    * `DuplicateCommand#execute` will pass the `toAdd` to the `model#addDuplicatePerson`, where `UniquePersonsList`
        is updated with the duplicated person.
    * After duplicating, the model will be updated to reflect the newly sorted contacts list, 
        alongside a return statement to provide confirmation to the user.

<puml src="diagrams/DuplicateSequenceDiagram.puml" />

#### Design consideration:
`DuplicateCommandParser` interface was implemented to adhere to SOLID principles, particularly the Single Responsibility 
Principle and Interface Segregation Principle.
* Single Responsibility Principle
    * The class maintains single responsibility by defining methods for duplicating persons without burdening
      implementations with unrelated methods
      <br>
* Interface Segregation Principle
    * Segregates behavior for duplicating into distinct methods `addDuplicatePerson` and `getPerson`, 
      thus, allowing `DuplicateCommand` to implement only the methods they need, rather than being forced to 
      implement monolithic interface with unnecessary methods.
      <br>
* **Alternative 1** `DuplicateCommand` constructor of the `DuplicateCommand` to take in `toAdd` as its parameter.
    * Pros: Straightforward design and easy to implement.
        * Duplication logic interacts directly with data structure being sorted.
      <br>
* **Alternative 2** `DuplicateCommand` constructor to take in all parameters of newly inserted person (name, address etc.)
  * Pros: Duplicating can be applied to different data structures without modification
    * Promoting code reuse and scalability.
  * Cons: Unnecessary complexity burden on `DuplicateCommand` to parse the user inputs into a `Person` and then execute the command.
    <br>

Alternative 1 is chosen for the following reasons:
* Simplicity: keeps duplicating logic simple and focused by directly interacting with the data structure(s) required.
* Clear Responsibility: Duplication logic is closely tied to the data structure it operates on, adhering to the Single
  Responsibility Principle.
* Ease of implementation: No need to pass unnecessary parameters to the `DuplicateCommandParser` method.
    * Reduce complexity and potential dependencies.

### Overwrite feature

#### Implementation

The feature to be able to overwrite a contact in the address book is facilitated by the use of the
`OverwriteCommand`, given that the target contact's name already exists in LookMeUp. 
It implements the following operations:
* `OverwriteCommand#`: Constructor class which is instantiated and stores the necessary `toAdd` person object
  based on user input.
* `OverwriteCommand#Executes`: Executes the necessary `setDuplicatePerson` method and updates the model.

The sorting mechanism consists of several components:
1. `setDuplicatePerson`: A method bound by the `Person`, `ModelManager`, `AddressBook` classes that each contain
   similar logic to support a SLAP form of implementation for the end execution point i.e. `execute` in
   `OverwriteCommand`.
2. `OverwriteCommand`: Initiates the overwriting by parsing user input to determine the identity of the person to add.
   After overwriting, it then updates the list of persons in the model.

Given below is an example usage scenario and how the feature mechanism behaves at each step.

* Step 1: The user launches the application for the first time, no contacts will be present in the `AddressBook`.
  When user `add` contacts in the `AddressBook`, contacts will be sorted based on their timestamp.

* Step 2: The user reaches a point where they encounter the need to overwrite an existing contact, but has forgotten that they already have
contact in their `AddressBook`, just with some differing details like address or email.

* Step 3: To continue, the user executes `add /n... /e ...` to attempt to add this seemingly new person.

* Step 4: The user then receives an error in their `AddressBook` which alerts them that they already have such a person
  in their `AddressBook`, and they have the option of overwriting the existing contact.

* Step 5: The user picks their choice and edits the command in their current `CommandBox`, replacing `add` with `overwrite INDEX`, 
leaving the rest of the arguments untouched.

* Step 6: The user executes `overwrite INDEX /n... /e...` command.
  * The `OverwriteCommand#` constructor will initialize with the `toAdd` variable based on the created `Person`
    object in `OverwriteCommandParser`, as well as the user's inputted index of person to be edited in the
    `AddressBook`.
  * `OverwriteCommand#execute` will pass the `indexOfTarget` to the `model#getPerson`, and will also pass the `toAdd`
    to the `model#setDuplicatePerson`, where `UniquePersonsList` is updated with the duplicated person.

<puml src="diagrams/OverwriteSequenceDiagram.puml" />

#### Design consideration:
`OverwriteCommandParser` class was implemented to adhere to SOLID principles, particularly the Single Responsibility
Principle and Interface Segregation Principle.
* Single Responsibility Principle
  * The class maintains single responsibility by defining methods for overwriting persons without burdening
    implementations with unrelated methods
    <br>
* Interface Segregation Principle
  * Segregates behavior for overwriting into distinct methods `setDuplicatePerson` and `getPerson`,
    thus, allowing `OverwriteCommand` to implement only the methods they need, rather than being forced to
    implement monolithic interface with unnecessary methods.
    <br>
* **Alternative 1** `OverwriteCommand` constructor of the `OverwriteCommand` to take in `toAdd` as its parameter.
  * Pros: Straightforward design and easy to implement.
    * Overwriting logic interacts directly with data structure being sorted.
      <br>
* **Alternative 2** `OverwriteCommand` constructor to take in all parameters of newly inserted person (name, address etc.)
  * Pros: Overwriting strategy can be applied to different data structures without modification
    * Promoting code reuse and scalability.
  * Cons: Unnecessary complexity burden on `OverwriteCommand` to parse the user inputs into a `Person` and then execute the command.
    <br>

Alternative 1 is chosen for the following reasons:
* Simplicity: keeps overwriting logic simple and focused by directly interacting with the data structure(s) requ ired.
* Clear Responsibility: Overwriting logic is closely tied to the data structure it operates on, adhering to the Single
  Responsibility Principle.
* Ease of implementation: No need to pass unnecessary parameters to the `OverwriteCommandParser` method.
  * Reduce complexity and potential dependencies.

### Filter feature

#### Implementation

The sorting mechanism is facilitated by `FilterCommand`. It implements the following operations:
* `FilterCommand#`: Constructor class which is instantiated and stores the necessary `Predicate` based on user input.
* `FilterCommand#Executes`: Updates the model based on the generated `Predicate`.

The filtering mechanism consists of several components:
1. `Predicate`: An instance of the `TagContainsKeywordsPredicate` class that stores the list of tags that a user inputs.
2. `FilterCommand`: Initiates the sorting by parsing user input to determine the filtering criteria and 
then updates the list of persons in the model via `model#updateFilteredPersonList`.

Given below is an example usage scenario and how the filter mechanism behaves at each step.

* Step 1: The user launches the application for the first time, no contacts will be present in the `AddressBook`.
  When user `add` contacts in the `AddressBook`, contacts are originally sorted based on their timestamp. Assume that 
  after adding contacts, the user wants to filter by contacts that are tagged as friends.

* Step 2: The user executes `filter friends` command.
  * The `filterCommand#` constructor will initialise and store the argument into `predicate`.
  * `filterCommand#execute` will pass the `predicate` to `model#updateFilteredPersonList`, where `UniquePersonsList`
    will be obtained and filtered by the given `predicate`
  * After filtering, the model will be updated to reflect the newly filtered contacts list, alongside a return statement
    to provide confirmation to the user.

    <puml src="diagrams/FilterSequenceDiagram.puml" alt="FilterSequenceDiagram" />
  
    <puml src="diagrams/FilterActivityDiagram.puml" alt="FilterActivityDiagram" />

#### Design consideration:
`TagContainsKeywordsPredicate` class was implemented for filtering functionality to adhere to SOLID principles, particularly the
Single Responsibility Principle, Interface Segregation Principle and Open/Close Principle.
* Single Responsibility Principle
  * The class maintains single responsibility by defining the list of all tags that the user input, as well as testing for tag matches
    without burdening implementations with unrelated methods
  <br>
* Interface Segregation Principle
  * Segregates behavior for filtering into the method `test`, thus, allowing different filtering strategies to implement only the methods they need.
    <br>
* **Alternative 1 (current choice)** `FilterCommand` constructor to take in `predicate` as its parameter.
  * Pros: Straightforward design and easy to implement.
    * Filtering logic interacts directly with creation of new `TagContainsKeywordsPredicate`, before passing it directly to the `FilterCommand`
    for execution.
  <br>
* **Alternative 2** `FilterCommand` constructor to take in user input string as its parameter.
  * Pros: Filtering strategies can be applied to different data structures without modification
    * Promoting code reuse and scalability.
* Cons: Unnecessary complexity burden on `FilterCommand` to parse the user input and then execute the command.
  <br>
Alternative 1 is chosen for the following reasons:
* Simplicity: keeps filtering logic simple and focused by directly interacting with the data structure, list in this case.
* Clear Responsibility: Filtering logic is closely tied to the data structure and class it operates on, adhering to the Single
  Responsibility Principle.
* Ease of implementation: No need to pass unnecessary parameters to the filter method.
  * Reduce complexity and potential dependencies.


### Copy User Info
#### Implementation

The `copy` command feature enhances a user experience by allowing the easy transfer of a contact's personal details directly into the clipboard of the user's operating system. The class, `CopyCommand`, is an inheritance of the `Command` class, that facilitates the process of copying essential information like a contact's name, email, and address, among other details. It offers users the flexibility to specify and copy multiple pieces of information simultaneously. The following example demonstrates how this command operates:

1. A user types in `copy 1 name email` into the text field of `CommandBox`. `Logic` is subsequently called to execute the command, where [**`AddressBookParser`**](https://github.com/AY2324S2-CS2103T-T12-2/tp/blob/master/src/main/java/seedu/address/logic/parser/AddCommandParser.java) would parse the input and return a `CopyCommand` (kindly refer to [here](#logic-component) for Logic design).


2. When `AddressBookParser#parseCommand()` is called, it makes use of switch statements to match the `copy` command and calls [**`CopyCommandParser#parse()`**](https://github.com/AY2324S2-CS2103T-T12-2/tp/blob/master/src/main/java/seedu/address/logic/parser/CopyCommandParser.java). `CopyCommandParser#parse()` is solely responsible for: (i) checking if input argument is empty; (ii) checking if index provided is non-negative; and (iii) calling [**`ParserUtil#parseFieldsToCopy()`**](https://github.com/AY2324S2-CS2103T-T12-2/tp/blob/master/src/main/java/seedu/address/logic/parser/ParserUtil.java) to verify that fields provided by the user are of acceptable fields. By definition, acceptable fields includes only `name`,`phone`,`email` and `address`.

**Note:** `ParseException` will be thrown and an error message will be shown to user if either (i) or (iii) is violated, while `IndexOutOfBoundsException` is thrown when (ii) is violated.

3. `CopyCommandParser#parse()` returns an instantiation of `CopyCommand` where its constructor takes in `Index` and a `List<String> fieldsToCopyList` as arguments. When the constructor of `CopyCommand` is called, the constructor removes any duplicated values (if any) from `fieldsToCopyList` with the use of Java Streams. Refer to code snippet below:

```Java
// Constructor
public CopyCommand(Index targetIndex, List<String> fieldsToCopyList) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.fieldsToCopyList = fieldsToCopyList.stream()
                                  .distinct()
                                  .collect(Collectors.toList());
    }
```


4. When `CopyCommand#execute()` is called, the contact of interest is obtained with the aid of `Model#getPerson` and the string representation of the information to be copied is retrieved by `CopyCommand#getInfo(Person person)` that returns a `StringBuilder`. The returned `StringBuilder` is then converted to `StringSelection` where it would be set as the content of `Clipboard`. The code snippet below shows the implementation of `CopyCommand#execute()`:

```Java
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        int zeroBasedIndex = targetIndex.getZeroBased();
        int addressBookSize = model.getAddressBook().getPersonList().size();

        if (zeroBasedIndex < 0 || zeroBasedIndex >= addressBookSize) { // checks if Index exceeds list of contacts
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND); 
        }

        Person person = model.getPerson(targetIndex.getZeroBased()); // retrieves Person of interest
        StringBuilder result = getInfo(person); // retrieves a person's information

        StringSelection toCopyString = new StringSelection(result.toString().trim());
        clipboard.setContents(toCopyString, null); // sets result to clipboard content

        return new CommandResult(MESSAGE_SUCCESS, false, false);
    }
```

Below is a sequence diagram of the overall `Copy` operation:

<puml src="diagrams/CopySequenceDiagram.puml" alt="CopyCommandSequenceDiagram"/>

#### Design Considerations
Several design considerations were taken into account when implementing the copy feature. Below lists a few alternatives:

- Alternative 1 (current choice): User keys in command to copy a contact's information.
  - Pros:
    - Does not violate product requirement (i.e. for typists).
    - Does not involve the interaction of Java FXML.
    - Testability works well with JUnit.
  - Cons:
    - Much harder to implement as SOLID principles have to be upheld.
    

- Alternative 2: Utilize JavaFX to allow users to select which information to copy.
  - Pros:
    -  Simpler design and implementation of functionality, as compared to alternative 1.
  - Cons:
    - Violates product requirement (not suitable for target audience).
    - Much harder to test, requires external API such as TextFX to test.

<br>

From the two alternatives, alternative 1 was ultimately conceived as it does not violate our product's requirements. 
We believe that this design choice would benefit typists who wish to utilise input commands to retrieve contact information.

### Exit Window
#### Implementation
For this feature, an exit window [`ExitWindow`](https://github.com/AY2324S2-CS2103T-T12-2/tp/blob/master/src/main/java/seedu/address/ui/ExitWindow.java) is created to seek confirmation from user to terminate LookMeUp. `ExitWindow` is packaged under `UI` , along with other various parts of Ui components e.g. `CommandBox`, `ResultDisplay`, and `PersonList` etc. Similar to other Ui components, `ExitWindow` inherits from `UiPart` which captures the commonalities between classes that represent the different part of the entire GUI.

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The Ui layout of `ExitWindow` is defined under [`ExitWindow.fxml`](https://github.com/AY2324S2-CS2103T-T12-2/tp/blob/master/src/main/resources/view/ExitWindow.fxml). Below elucidates how `ExitWindow` is used:
1. User executes the command `exit`, or other similar commands that resolves to `exit` deemed by the [fuzzy input algorithm](#fuzzy-input).
2. An exit window will appear prompting for user confirmation to exit - Yes/No button.
3. User would select either one of the 2 options.

<puml src="diagrams/ExitCommandActivityDiagram.puml" alt="Flow of Exit command"/>

In `ExitWindow.fxml`, the `Yes` button is set as the default button such that the button receives a VK_ENTER press; the `Yes` button will always be in focus whenever `ExitWindow` is displayed. When a positive confirmation is received, `ExitWindow#yesButton()` would be called to terminate LookMeUp.

Consequently, the `No` button is set as the cancel button where it would receive a VK_ESC press that hides `ExitWindow`. `ExitWindow#NoButton()` would be called when a negative confirmation is received.

The behaviour of these implementations follow the behaviours as specified by [JavaFx](https://openjfx.io/javadoc/11/javafx.controls/javafx/scene/control/Button.html).

#### Design Considerations
- Single Responsibility Principle
  - The `ExitWindow` maintains the responsibility of displaying exit confirmation and handling a user choice, which reduces coupling between itself and other Ui components.
  

- Alternate implementation: A text field input that requires user to enter yes/no for confirmation. This design was not conceived as it requires the handling of invalid input, as is not as simple to implement as compared to the current implementation. Moreover, confirmation utilizing buttons is more intuitive for majority of users.

### Input History Navigation
#### Implementation

This Ui feature allow users to restore previously entered commands typed in the [`CommandBox`](https://github.com/AY2324S2-CS2103T-T12-2/tp/blob/master/src/main/java/seedu/address/ui/CommandBox.java), regardless of the validity of the command. Similar to the CLI, users would use the Up/Down arrow keys to navigate previously typed commands in the input history.

The class that encapsulates all the history of the commands is `InputHistory` which is declared as a nested class inside `CommandBox`; this is because the history of commands should be the responsibility of `CommandBox` class and should not be openly accessible to other classes.

`InputHistory` is instantiated whenever the constructor of `CommandBox` is called. As such, there is an association between `InputHistory` and `CommandBox`. The implementation of `InputHistory` encapsulates an `ArrayList<String>` and an index-pointer. Whenever a command is received by `CommandBox`, the command typed will be stored inside `InputHistory` (regardless of validity), as shown by the code snippet below:

```Java
@FXML
public class CommandBox extends UiPart<Region> {
    
    ///Handles the event whenever a command is entered.
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText); //execute command in Logic
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        } finally {
            inputHistory.addToInputHistory(commandText); 
            commandTextField.setText(""); // clears the textfield
        }
    }
}
```

`CommandBox#handleArrowKey()` is called when a `KeyEvent` is detected by JavaFX event listener. With reference to the code snippet below, the function checks if `InputHistory` is empty. If the history is empty, it performs nothing. Else, it checks if whether the key pressed is an Up key, or a Down key. The code snippet below shows the implementation of `CommandBox#handleArrowKey()`:

```Java
private void handleArrowKey(KeyEvent event) {
        String keyName = event.getCode().getName();
        
        //Performs nothing if there is no history.
        if (inputHistory.inputList.isEmpty()) {
            return;
        }
        if (keyName.equals("Up")) {
            inputHistory.decrementIndex(); //Reduces pointer by 1
            setTextField(); // Sets textfield according to pointer
        }
        if (keyName.equals("Down")) {
            inputHistory.incrementIndex(); //Increment pointer by 1
            setTextField(); //Sets textfield according to pointer
        }
}
```

When `CommandBox#setTextField()` is called, it requests for the command from `InputHistory#getCommand()` that is pointed by the pointer, and sets the text field of `CommandBox` that is returned by the method.

How the `InputHistory` index-pointer works:
- Whenever a new command has been entered, the command is added into the list. The index-pointer is set to the **size** of the `ArrayList` (i.e. it is pointing towards an empty slot in the `ArrayList`).


- During an Up key press, the index-pointer is decremented by one (i.e. it is pointing towards an earlier command in the history).


- During a Down key press, the index-pointer is incremented by one (i.e. it is point towards a later command in the history).

Below is a sequence diagram when an **Up key** is pressed:

<puml src="diagrams/InputHistorySequenceDiagram.puml" alt="UpKeySequenceDiagram"/>

#### Design Considerations
- Single Responsibility Principle 
  - `CommandBox` and `InputHistory` are gathered together as the two classes share the responsibilities of receiving and retrieving user inputs within the text field, hence increasing the overall cohesion of Ui components.
  

- `inputHistory` is set as a private variable as no other class should have access to the internal of the class, except `CommandBox` itself. This allows encapsulation and information-hiding from other classes. Setter and Getter methods of `InputHistory` such as `decrementIndex()`, `incrementIndex()` and `addToInputHistory()` etc. serve as functions to retrieve and modify the value of the class.


- Both `InputHistory#decrementIndex()` and `inputHistory#incrementIndex()` are designed with guard clauses to prevent the index pointer from reducing below zero or exceeding beyond the bounds of the `ArrayList<String>`.


- Alternative Design
  - Currently, the implementation of `InputHistory` consists of an `ArrayList<String>` that stores all previously typed commands. An alternative solution to using an ArrayList would be LinkedList. However, LinkedList is not adopted as Java's LinkedList is implemented as Doubly-linked list which causes more memory overhead than ArrayList. Moreover, due to regular access of elements in the collection, ArrayList is a better design decision as its `get` operation runs in constant time O(1), as compared to LinkedList `get` O(n). Other methods such as `remove` and `search` etc. were not considered in the design decision as these operations are not needed for implementing `InputHistory`, but may be relevant for future extensions to the class.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:
NUS students who stay on campus

## User Stories
**Value proposition**:
1. Keeps track of the location and details specific to each contact, knowing who to make calls with
2. Given how students who stay on campus find themselves in many different committees and interest groups, our Address Book seeks to provide features that allows them to compartmentalise their contacts and access various groups easily


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                 | So that I can…​                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | Student in a lot of committees | Access my contacts by groups | Easily identify the people in their different committees and CCAs                 |
| `* * *`  | Student                                       | Sort the contacts alphabetically | Easily navigate the address book                                                        |


### Use cases

(For all use cases below, the **System** is `LookMeUp` and the **Actor** is the `user`, unless specified otherwise)

**Use case:** UC1 - Add a contact\
**Person that can play this role:** Student in a lot of committees

**MSS**

1. User types `add` command.
2. LookMeUp adds the contact and displays the new contact in the database.\
    Use case ends.

**Extensions**
* 1a. User typed the `add` command with an invalid format or field
    * 1a1. LookMeUp displays the error.
    * 1a2. User enters the `add` command again

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.

**Use case:** UC2 - Remove a contact\
**Person that can play this role:** Student in a lot of committees

**MSS**

1. User types `remove` command
2. LookMeUp requests for confirmation.
3. LookMeUp removes the contact and displays an execution success message.\
   Use case ends.


**Extensions**
* 1a. User typed the `remove` command with an invalid format or field
    * 1a1. LookMeUp displays the error.
    * 1a2. User enters the `remove` command again

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.


* 3a. User declines the removal of contact.
    * 3a1, LookMeUp confirms user's selection.\
      Use case ends.

**Use case:** UC3 - Filter contacts by tags\
**Person that can play this role:** Student in a lot of committees

**MSS**

1. User type filter contacts command
2. LookMeUp displays the contact in the database\
Use case ends.

**Extensions**
* 1a. User typed an invalid command
    * 1a1. LookMeUp displays the error.
    * 1a2. User enters the correct command.

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.

**Use case:** UC4 - Sort contacts by tags\
**Actor:** User\
**Person that can play this role:** Student in a lot of committees

**MSS**

1. User type sort contacts command
2. LookMeUp displays the contact in the database\
Use case ends.

**Extensions**
* 1a. User typed an invalid command
    * 1a1. LookMeUp displays the error and shows a list of commands it supports.
    * 1a2. User enters the correct command.

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.


**Use case:** UC5 - Formatting an Add Command with system prompts\
**Person that can play this role:** Student who is unfamiliar with the format of the Add command

**MSS**

1. User type addbystep command
2. LookMeUp prompts for details
3. User enters the requested details
4. LookMeUp will display the success message, and will prompt the user to type the copy command (`cp`)
5. User types the copy Comand\
    Use case ends.

**Extensions**
* 2a. User types an invalid detail.
    * 2a1. LookMeUp displays the error.
    * 2a2. User enters the detail again. 

  Steps 2a1-2a2 are repeated until the command entered is correct.\
  Use case resumes from step 3.

* 4a. User types a input that is not the copy command.
    * 4a1. LookMeUp will prompt the user to type the copy command.
    * 4a2. User enters another input. 

  Steps 4a1-4a2 are repeated until the copy command is entered.\

**Use case:** UC6 - Editing a command\
**Person that can play this role:** Student who wishes to update the contact details of a contact

**MSS**

1. User types the `edit` command.
2. LookMeUp edits the details of the contact and displays the new contact in the database.\
    Use case ends.

**Extensions**
* 1a. User typed the `edit` command with an invalid format or field
    * 1a1. LookMeUp displays the error.
    * 1a2. User enters the `edit` command again

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.


**Use case:** UC7 - Copying a contact's details\
**Person that can play this role:** Student who wishes to copy the contact details of a contact

**MSS**

1. User types the `copy` command.
2. LookMeUp copies the details of the contact specified by the user and displays the success message.\
    Use case ends.

**Extensions**
* 1a. User typed the `copy` command with an invalid format or field
    * 1a1. LookMeUp displays the error.
    * 1a2. User enters the `copy` command again

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.


**Use case:** UC8 - Undoing the last command\
**Person that can play this role:** Student who entered a wrong command and wishes to revert his previous command

**MSS**

1. User types the `undo` command.
2. LookMeUp reverts the command entered by the user and displays the success message.\
    Use case ends.

**Extensions**
* 1a. User typed the `undo` command with no previous state-changing commands
    * 1a1. LookMeUp displays the error.\
  Use case ends.

**Use case:** UC9 - Redoing an undo command\
**Person that can play this role:** Student who wishes to redo the previous undo command

**MSS**

1. User types the `redo` command.
2. LookMeUp reverts the latest undo command and displays the success message.\
    Use case ends.

**Extensions**
* 1a. User typed the `redo` command with no previous undo command.
    * 1a1. LookMeUp displays the error.\
  Use case ends.

**Use case:** UC10 - Clearing all the contacts in LookMeUp\
**Person that can play this role:** Student who wants to delete all the contacts in LookMeUp

**MSS**

1. User types the `clear` command.
2. LookMeUp edits clears all the contacts and displays the success message to the user.\
    Use case ends.

**Use case:** UC11 - Exiting the application\
**Person that can play this role:** Student who wishes to exit the application.

**MSS**

1. User types the `exit` command.
2. LookMeUp displays a the Exit window
3. User clicks "yes"
4. LookMeUp closes\
   Use case ends.


**Extensions**

* 3a. User clicks "no".
    * 3a1, LookMeUp confirms user's selection.\
      Use case ends.

**Use case:** UC12 - Retrieving a person by name \
**Person that can play this role:** Student who wants to look up a specific contact in LookMeUp

**MSS**

1. User types the `find` command.
2. LookMeUp displays a list of people who watches the details entered by the user.
   Use case ends.

**Use case:** UC12 - Overwriting a contact in LookMeUp\
**Person that can play this role:** Student who wishes to completely change the the details of an existing contact.

**MSS**

1. User types the `overwrite` command.
2. LookMeUp changes the contact and displays the new contact in the database.\
    Use case ends.

**Extensions**
* 1a. User typed the `overwrite` command with an invalid format or field
    * 1a1. LookMeUp displays the error.
    * 1a2. User enters the `overwrite` command again

  Steps 1a1-1a2 are repeated until the command entered is correct.\
  Use case resumes from step 2.







    







  
      

## Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  A user should be able to add contacts even if they are not IT-savvy.
5.  Any operation executed on the app (list, remove, add, etc) should not take more than 10 minutes to process.
6.  The startup time for the application should not take more than 10 minutes.
7.  Side pop-up windows should not interfere with the execution of commands in the main window.


## Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **IT-savvy**: The user is not familiar with the exact format of the add command.
* **Side pop-up window**: Additional windows that can be opened by the user during usage of the software(e.g. the help window).
* **SLAP**: The Single Level of Abstraction Principle states that all the code inside a method should be at the same level of abstraction.
* **SOLID principle**: The SOLID principle is a set of five design principles used in object-oriented programming to make software designs more understandable, flexible, and maintainable. The acronym SOLID stands for:
    * Single Responsibility Principle
    * Open/Closed Principle
    * Liskov Substitution Principle
    * Interface Segregation Principle
    * Dependency Inversion Principle
* **Levenshtein distance**: Measure of the difference between two strings, representing the minimum number of single-character edits (insertions, deletions, or substitutions) required to change one string into the other.
* **BK-Tree**: A tree data structure used to efficiently store and search for strings or other data based on their edit distance or similarity.



--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Add By Step 

Loading up the AddByStep Window

1. Type `addbystep` into LookMeUp
    * Expected output: A new window should appear, prompting you for the name of the person to enter 
2. Leave the name blank and press the ENTER key 
    * Expected output: An error message should appear, and you have to enter the name again 
3. Type `John` into the GUI 
    * Expected output: The name will be successfully accepted, and you will be prompted for the next field
4. You may follow the prompts to enter the subsequent details, examples of invalid inputs are given in the example use
case scenario in Add By Step.

### Edit contact

Edit contact based on the details provided. eg. Name, email, address etc.

1. Prerequisites: Existing LookMeUp contacts list must not be empty.

   (Details of name, address etc. are a placeholder for the following test cases)

2. Test case: `edit 1 n/Alex Yeoh`

   Expected: Contact at index 1's name has been edited to Alex Yeoh.

3. Test case: `edit 2 n/Bernice Yu p/91725373`

   Expected: Contact at index 2's name and number have been edited to Bernice Yu, 91725373 respectively.

### Filter contact list

Filter contact list based on the tag(s) provided.

1. Test case: `filter friends`
    Expected: Only contacts that have the tag `friends` will be shown in the contact list

2. Test case: `filter Neighbours`
   Expected: Only contacts that have the tag `Neighbours` will be shown in the contact list

### Duplicate 

Add a person that has an **identical** name to a contact in your existing LookMeUp contacts.

1. Prerequisites: Existing LookMeUp contacts list must not be empty. 

    (Details of name, address etc. are a placeholder for the following test cases) 
2. Test case: `duplicate n/Alex Yeoh a/Serangoon Crescent Street e/alexyo@example.com p/91234567`

   Expected: Contact with above details (Name as Alex Yeoh, Phone as 91234567...) is added

3. Test case: `duplicate n/Bernice Yu a/Serangoon Crescent Street e/berniceyu@example.com p/91234568`

   Expected: Contact with above details (Name as Bernice Yu, Phone as 91234568...) is added

4. Test case: `duplicate n`

   Expected: No contact is added. Error details are shown in the status message.

### Overwrite

Overwrites a person that has an **identical** name to a contact in your existing LookMeUp contacts.

1. Prerequisites: Existing LookMeUp contacts list must not be empty.

   (Details of name, address etc. are a placeholder for the following test cases)
2. Test case: `overwrite 1 n/Alex Yeoh a/Serangoon Crescent Street e/alexyo@example.com p/91234567`

   Expected: Contact at index 1, and with above details (Name as Alex Yeoh, Phone as 91234567...) is overwritten

3. Test case: `overwrite 2 n/Bernice Yu a/Serangoon Crescent Street e/berniceyu@example.com p/91234568`

   Expected: Contact at index 2, and with above details (Name as Bernice Yu, Phone as 91234568...) is overwritten

4. Test case: `overwrite 1`

   Expected: No contact is overwritten. Error details are shown in the status message.

### Safe Removal of a Person

1. Removing a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: With a list of at least 2 contacts, enter `remove 2`<br>
      Expected: Third contact is spotlighted from the list. Prompt to confirm removal with `yes`/`no`.
      * If `yes`, the contact will be removed. A success message and details of the removed contact will be shown in the status message. Timestamp in the status bar is updated.
      * If `no`, the contact will not be removed. Status message will show the removal is aborted. Status bar remains the same.

   1. Test case: `remove 0`<br>
      Expected: No person is removed. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect remove commands to try: `remove`, `remove x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Fuzzy Input

Handling a command with a single misspelled letter.

  1. Test case: `lort name`
      <br>
     Expected: `sort name` command will still be executed and contact list will be sorted alphabetically based on person's name
  2. Test case: `lst`
     <br>
     Expected: `list` command will still be executed and all the contacts will be listed in the contact list. 
  3. Other misspelled command to try: `fwlter TAG`, `adbystep`, `...`
     <br>
     Expected: Correctly spelled commands will still be executed as intended.

### Sorting contact list

Sort contact list based on the keywords input.

  1. Test case: `sort name`
     <br>
     Expected: Contact list will be sorted lexicographically based on person's name.

  2. Test case: `sort tag`
     <br>
     Expected: Contact list will be sorted lexicographically based on person's tags.

### Copy Contact Information

Retrieve a contact's information into system clipboard.

Given this example:<br>
<p align = "center">
    <img src="images/example.png" width="50%"/><br>
</p>

Below shows a list of possible commands:

| Sample Commands                   | Details                               | Results                                   |
|-----------------------------------|---------------------------------------|-------------------------------------------|
| `copy -1 name`                    | Copies the name of contact indexed -1 | `N.A.` Error will be shown.               |
| `copy 4 tag`                      | Copies the tag of contact indexed 4    | `N.A.` Tag is not a valid field.          |
| <code> copy &nbsp; 4 name </code> | Extra spaces between `copy` and index | `Taylor Sheesh`                           |
| <code> copy 4 &nbsp; name </code> | Extra spaces between index and `name` | `N.A` Error prompt fields not recognised. |

For more sample test cases, kindly refer to the [UG](https://ay2324s2-cs2103t-t12-2.github.io/tp/UserGuide.html#copies-a-person-information-to-clipboard-copy).

### Undo / Redo 

1. Enter `add n/Jia wei p/97743772 e/jw@gmail.com a/Block E 02-22 t/friend` in the command box.

Expected output: A new contact named "Jia wei" will be added to your list, and will be found at the last index.  

2. Enter `undo` in the command box.

Expected output: The contact list will revert back to its state before the contact was added in Step 1.

3. Enter `redo` in the command box.

Expected output: The contact list will revert back to the state after the contact was added as it is in Step 2.

4. Within the same application launch, you may try to perform **n** consecutive **state-changing commands**, then 
**directly followed by** `undo`, and expect to be able to run `undo` **n consecutive times** as well. 
Similarly, with **x** consecutive `undo` commands, you should be able to run `redo` consecutively **x** times as well.

<box type="info" seamless>

**Note:** Examples of commands that are NOT state-changing include: `filter`, `list` and hence if you try to `undo`, 
there will be an error message that there is no command to undo. 

Do also note that the `redo` command must be immediately preceded with `undo`, failing which (false example: 
`add n/...` , `undo`, `remove 1`, then `redo`) there will be an error message that there is no command to redo. 

</box>

### Saving data

1. Dealing with missing/corrupted data files

   1. Open the .json file where the details of the contact have been started
   2. Go to the name of the first person, and remove the name (This will corrupt the data file as the name cannot be blank)
   3. Run LookMeUp, and execute a command (any command will do)
   4. Expected output: LookMeUp will load up blank, and after the execution of the command, the corrupted json file will be erased.

### Planned Enhancements

Our team consists of 5 members. 

1. **Add an `exit` command to `AddCommandHelper` to enhance AddByStep process**
   * Currently, `AddCommandHelper` has to be closed manually, which is not optimised for fast typists. 
   * We plan to add an exit command to `AddCommandHelper` such that you can close the window simply by typing the 
   `exit` command


2. **Vary Distance Metric for Fuzzy Input**
   * Currently, the MAX_DISTANCE for the distance metric is set to 1. 
   * To enhance user-experience and accommodate longer commands with potentially more misspellings, it would be advantageous to dynamically adjust the MAX_DISTANCE according
to the length of the correct command string. 
   * This approach allows a more flexible and adaptable matching process,
guaranteeing that the misspelling tolerance varies proportionately with command length. 
   * By dynamically adjusting the MAX_DISTANCE, longer and more complex input command like `addbystep` can be accurately \
   identified. 


3. **Enhance Invalid Input Error-Handling for Safe-Removal Confirmation Step**
   * Currently, when a user is prompted for confirmation, to enter either a `yes` or `no` input, if they enter any other 
   (invalid) input e.g. `abc`, they will be prompted with an `Unknown Command` error message, which is too general.
   * With the `Unknown Command` prompt, though the GUI still shows the spotlighted contact for removal, the user is 
   unable to directly type `yes`/`no` to proceed with the confirmation as the system does not recognise that the user is
   still in the process of removing the contact, which brings slight inconvenience though there is a simple step to
   get around it by typing `remove 1` then `yes`/`no` to proceed with the confirmation.
   * **2 Enhancements**:
        * We plan to improve the `Unknown Command` error message to be more specific, such as `Invalid Input, please 
        enter 'yes' if you wish to proceed with the removal, and 'no' if you wish to abort the removal process`.
        * Together with this, we are also planning to change the internal validity checking process of 
        `RemoveConfirmation#isValidInput` to check for whether the **last VALID input** is a `RemoveCommand` 
        instead of simply checking the **last input** (which currently makes the system prone to keeping track of the 
        invalid input and assuming the user is no longer doing a removal). This would remove the inconvenience of having 
        to type `remove 1`. 

4. **Improve Name Validation for Name Field**
   * Currently, names can only contain alphanumeric characters and spaces. Legal names such as `Joseph King Jr.`, 
   `Shaquille O'Neal`, `Mary-Anne Tan` or `Ravichandram S/O Ramesh` are considered invalid. 
   * We plan to update the validation regex to enable special characters such as `'`, `-`, `/`, and `.` to be recognised
   * This allows LookMeUp to accommodate to more users, and to be more inclusive.

