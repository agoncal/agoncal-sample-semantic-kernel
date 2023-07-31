# Samples using Semantic Kernel

These samples are used to test the [Semantic Kernel](https://github.com/microsoft/semantic-kernel).
For that, you either need an OpenAI key or an Azure OpenAI key.
These samples use Azure OpenAI.

To get all the samples running you first need to have an Azure subscription and create the Azure OpenAI resources with `create_azure_ai_resources.sh`.
Then you need to set the API key in `conf.properties`.

## Simple

Simple examples that show how to use the Semantic Kernel.

## Super Heroes

This sample shows how to use the Semantic Kernel to narrate a fight between a Super Hero and a Super Villain.
The prompt that we create looks like this:

```text
NARRATE THE FIGHT BETWEEN A SUPER HERO AND A SUPER VILLAIN.
DURING THE NARRATION DON'T REPEAT "super hero" OR "super villain" WE KNOW WHO IS WHO.
WRITE 4 PARAGRAPHS MAXIMUM.

THE NARRATION MUST BE:
- G RATED
- WORKPLACE/FAMILY SAFE
  NO SEXISM, RACISM OR OTHER BIAS/BIGOTRY

BE CREATIVE.

HERE IS THE DATA YOU WILL USE FOR THE SUPER VILLAIN:

+++++
name:   Darth Vader
powers: Accelerated Healing, Agility, Astral Projection, Cloaking, Danger Sense, Durability, Electrokinesis, Energy Blasts, Enhanced Hearing, Enhanced Senses, Force Fields, Hypnokinesis, Illusions, Intelligence, Jump, Light Control, Marksmanship, Precognition, Psionic Powers, Reflexes, Stealth, Super Speed, Telekinesis, Telepathy, The Force, Weapons Master
level:  13
+++++

HERE IS THE DATA YOU WILL USE FOR THE SUPER HERO:

+++++
name:   Chewbacca
powers: Agility, Longevity, Marksmanship, Natural Weapons, Stealth, Super Strength, Weapons Master
level:  5
+++++

HERE IS THE DATA YOU WILL USE FOR THE FIGHT:

+++++
Chewbacca HAS WON THE FIGHT AGAINST Darth Vader
+++++
```

The narration for the fight looks like this:

```text
The two opponents faced each other, ready to engage in a fierce battle. Chewbacca, with his massive size and strength, charged towards his opponent, while Darth Vader stood his ground, his red lightsaber ignited. Chewbacca swung his weapon, but Darth Vader was quick to dodge, using his agility to move out of the way. The two continued to exchange blows, each one trying to gain the upper hand.

Darth Vader used his telekinesis to throw objects at Chewbacca, but the Wookiee was too quick, dodging each one with ease. Chewbacca then used his natural weapons, his sharp claws and teeth, to attack Darth Vader, who used his force fields to protect himself. The two continued to fight, neither one giving up.

As the battle raged on, Chewbacca began to tire, his level 5 powers no match for Darth Vader's level 13 abilities. However, Chewbacca was determined to win, and he used his marksmanship to shoot at Darth Vader's lightsaber, causing it to malfunction. With his opponent disarmed, Chewbacca used his super strength to deliver the final blow, knocking Darth Vader to the ground.

The battle was over, and Chewbacca emerged victorious. He had proven that even with lesser powers, determination and skill could overcome any obstacle. As he walked away from the defeated Darth Vader, Chewbacca knew that he had done what was right, and that the galaxy was a safer place because of his actions.
```
