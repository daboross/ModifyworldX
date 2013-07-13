Customized fork of Modifyworld.
==

This is a highly experimental fork of Modifyworld, and probably won't end up anywhere.
==
* Removes:
 * PermissionsEx integration
 * Metadata permissions
 * modifyworld.login permission
 * modifyworld.chat permission
 * modifyworld.chat.private permission
 * modifyworld.use.\<item>.on.block.\<block> permissions
 * modifyworld.use.\<item>.on.entity.\<block> permissions
 * modifyworld.items.hold.<item> permissions
* Changes:
 * modifyworld.items.take.\<item>.of.\<container> is replaced with modifyworld.items.take.<item>
 * modifyworld.items.put.\<item>.of.\<container> is replaced with modifyworld.items.put.<item>
 * Changed entity node 'player.<username>' to 'player'
 * Works with .* nodes for ALL permissions plugins that work with SuperPerms
