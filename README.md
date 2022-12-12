# WolfDogUtilities

Providing the game with certain utilities for wolves, giving more control of the user for them.

+ 🦴 Limits on how many wolves each player can tame.
+ 🦴 Keeping track of how many wolves you have tamed.
+ 🦴 Limits on how many dogs you can train (with DoggyTalents installed)
+ 🦴 And more in the future!

-----------------

+ This mod is a **Server Side Only** mod and only required to be installed on the server.
+ DoggyTalentsNext is an optional dependency.
+ This will not work with the OG DoggyTalents because the re-naming of some classes.

-----------------

Configurations and features can be found in the ```wolfdogutilities-common.toml``` config file : 
```
  ["Wolf Dog Utilities"]
    #"Dog" refers to the dog from DoggyTalents.
    #If DoggyTalents is not installed, every config below involving "Dog" will simply have no effect.
    
    #This allows random wolves who dies in the wild to notify all player and get another chance to be tamed. 
    save_wild_wolf = false
    #This put a limit on how many wolves each player can tame.
    wolf_tame_limit = true
    #This define the limit of how many wolves each player can tame.
    #Range: > 1
    wolf_tame_limit_value = 7
    #This put a limit on how many dogs each player can train.
    dog_train_limit = false
    #This define the limit on how many dogs each player can train.
    #Range: > 1
    dog_train_limit_value = 7
    #Specifies whether wolves of the owner can breed while tame limit is reached.
    wolf_breed_over_limit = false
    #Specifies whether dogs of the owner can breed while train limit is reached.
    dog_breed_over_limit = false
    #This allows player to see how many wolves they got by Shift+RightClicking any wolves with a Bone in hand
    show_wolf_tame_count = true
    #This allows player to see how many wolves they have bred by Shift+RightClicking any wolves with a Poppy in hand
    show_wolf_breed_count = true
    #This allows player to see how many wolves have died by Shift+RightClicking any wolves with an Axe in hand
    show_wolf_death_count = true
```
