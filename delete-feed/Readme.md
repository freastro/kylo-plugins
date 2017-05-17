Delete Feed Plugin
==================

This plugin can be used to delete a specific feed from Kylo on startup.

To use this plugin, edit `FeedDeleteAction.java` and change the `FEED_ID` to the id of the feed to be deleted. Then compile `mvn package` and place the jar file into `/opt/kylo/kylo-services/plugin/`. Delete the jar once Kylo has finished starting.
