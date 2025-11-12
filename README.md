# IBM Cloud Logs Expander

This utility came when I needed to process the the logs exported by IBM Cloud Logs from the K8s cluster of a certain customer where the only scripting available was Java on a Citrix remote desktop.

You may export logs from the UI at the Settings button: it displays a menu which includes an export option. Placing the export option of a log viewer in the settings button is something that only twisted minds at Big Blue could think of (oh, BTW, I'm not affiliated with IBM but know some people there -with twisted minds, for sure, and also great guys to be friend of... cos I am a twisted mind as well- :)

Export the logs as a CSV (not JSON) and use parameters to filter the interesting columns.

Also use parameters to change the CSV column separator if, like me, your Excel locale uses some exotic separator as the semicolon. No, it does not produce tab separated files. That Citrix has Excel and we use it as the viewer (pro tip: use autofilter and have a drop down to filter DEBUG, INFO, ERROR, etc, twisted but smart mind).

This is the behavior recap by the AI who wrote the code thanks to my insightful prompts (twisted and smart mind, but also lazy).

- Creates a folder next to the ZIP named after the ZIP (e.g., myfiles.zip → myfiles/).
- Extracts all entries (preserving any internal folders) into that folder.
- For every .csv inside, writes a filtered copy in the same folder named: **filter_{first}_{num|all}_<original>.csv**
- Input CSV is parsed with your csvSeparator (default ,); output uses desiredSeparator (default ;).
- If numColumns=ALL (default), it keeps from firstColumn to the end.
- If a row is shorter than requested, it writes whatever exists (no crash).

Build & run

```
# Build
mvn -q -e -DskipTests package

# (optional) Run tests
mvn -q test

# Run the shaded JAR
java -jar target\csv-zip-filter-1.0.0.jar sample_zips\20251112b.zip
java -jar target\csv-zip-filter-1.0.0.jar sample_zips\20251112b.zip 7
java -jar target\csv-zip-filter-1.0.0.jar sample_zips\20251112b.zip 7 3 
java -jar target\csv-zip-filter-1.0.0.jar sample_zips\20251112b.zip 1 ALL '|'
java -jar target\csv-zip-filter-1.0.0.jar sample_zips\20251112b.zip 3 5 ',' ';'
```
