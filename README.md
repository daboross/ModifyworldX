Experimental fork of Modifyworld.
==

I am currently testing things out and getting Permissions to work in this fork of ModifyWorld.
==
* Current differences:
 * Removes:
  * PermissionsEx integration
  * Metadata permissions
  * modifyworld.login permission
  * modifyworld.chat permission
  * modifyworld.chat.private permission
  * modifyworld.use.\<item>.on.block.\<block> permissions
  * modifyworld.use.\<item>.on.entity.\<block> permissions
  * modifyworld.items.hold.\<item> permissions
  * entity name 'group.\<group>' removed
 * Changes:
  * modifyworld.items.take.\<item>.of.\<container> is replaced with modifyworld.items.take.\<item>
  * modifyworld.items.put.\<item>.of.\<container> is replaced with modifyworld.items.put.\<item>
  * entity name 'player.<player>' replaced with 'player'
  * entity names no longer include 'monster.', 'animal.', or 'npc.' in front of them.
  * Changed entity node 'player.\<username>' to 'player'
  * Changed tameable entity node from 'animal.\<entity>.\<owner>' to '\<entity>
  * Works with .* nodes for ALL permissions plugins that work with SuperPerms
