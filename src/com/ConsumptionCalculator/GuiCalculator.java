package com.ConsumptionCalculator;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


public class GuiCalculator extends JFrame { //Frame

    //Labels
    private JLabel labelOldKmReading;
    private JLabel labelCurrentKmReading;
    private JLabel labelTankedLiter;
    private JLabel labelErrorMsgOldKmReading;
    private JLabel labelErrorMsgCurrentKmReading;
    private JLabel labelErrorMsgTankedLiter;
    private JLabel labelResult;

    //TextFields
    private JTextField textFieldOldKmReading;
    private JTextField textFieldCurrentKmReading;
    private JTextField textFieldTankedLiter;

    //Buttons
    private JButton buttonCalculate;

    //menu
    private JMenuBar mainMenuBar;
    private JMenu fileMenu;
    private JMenuItem tankLevelsMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu editMenu;
    private JMenuItem resetTankLevelsMenuItem;
    private JMenuItem exportTankLevelsMenuItem;
    private JMenuItem importTankLevelsMenuItem;
    private JMenu helpMenu;
    private JMenuItem aboutMenuItem;
    private JMenuItem changeSettingsItem;

    //vars
    private ArrayList<Refuel> refuelHistory;
    private UserSettings userSettings;

    private static ResourceBundle i18n;
    private static final String fileNameTankLevels = "TankLevels.data";
    private static final String fileNameUserSettings = "UserSettings.data";
    private static final Locale defaultLocale = Locale.getDefault();


    public GuiCalculator(){

        loadUserSettings(); //loads User settings
        configureLanguage();


        setLayout(new GridBagLayout()); //set UI Layout to grid bag layout

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("calculator512.png"))); //set Icon

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(520,250);
        setResizable(true);
        initComponents(); //set definition and initial settings of the components
        updateTextOfComponents();

        addListeners(); //add listeners to objects

        initUi(); //set the location and nesting of UI objects

        setLocationRelativeTo(null); //app location in the middle of the screen
        setVisible(true);

//        System.out.println("App Ready");
    }

    private void configureLanguage(){
        if (userSettings.getUseSystemLocale()) {
            Locale.setDefault(defaultLocale);
            userSettings.setProgramLocale(defaultLocale);
        } else {
            Locale.setDefault(userSettings.getProgramLocale());
        }
        ResourceBundle.clearCache();
        i18n = ResourceBundle.getBundle("com.ConsumptionCalculator.i18n");

        UIManager.put("OptionPane.cancelButtonText", i18n.getString("genericCancel") );
        UIManager.put("OptionPane.yesButtonText", i18n.getString("genericYes") );
        UIManager.put("OptionPane.noButtonText", i18n.getString("genericNo") );
        UIManager.put("OptionPane.okButtonText", i18n.getString("genericOk") );
    }

    private void saveUserSettings(UserSettings userSettings){ //saves user settings
        try {
            saveObject(fileNameUserSettings, userSettings); //try to save the object via filename, a object is given as a reference
        } catch ( Exception exception) {
            JOptionPane.showMessageDialog(null, i18n.getString("exportFailMsg"), i18n.getString("exportFailTitle"), JOptionPane.WARNING_MESSAGE); //conformation for failed export
                exception.printStackTrace(); //prints our the exception - only in dev mode
        }
    }

    private void loadUserSettings(){ //loads user settings
        UserSettings userSettings = new UserSettings(); //new user settings object
        try {
            userSettings = (UserSettings) loadObject(fileNameUserSettings, userSettings); //try to load the object via filename, a object is given as a reference
        }catch (IOException ioException) {
            System.out.println(ioException);
            userSettings = new UserSettings();
        }
        this.userSettings = userSettings;
    }

    private void saveObject(String fileName, Object object){ //Method to save any type of Object
        ObjectOutputStream out = null; //new ObjectOutputStream
        try {
            out = new ObjectOutputStream(new FileOutputStream(fileName));
        } catch (Exception ioException) {
            ioException.printStackTrace(); //prints our the exception - only in dev mode
        }
        try {
            assert out != null;
            out.writeObject(object);
        } catch (IOException ioException) {
            ioException.printStackTrace(); //prints our the exception - only in dev mode
        }

        try {
            out.close();
        } catch (IOException ioException) {
            ioException.printStackTrace(); //prints our the exception - only in dev mode
        }
    }

    private Object loadObject(String fileName, Object object) throws IOException { //Method to load any type of Object
        File tmpDir = new File(fileName); //creates temp File
        boolean exists = tmpDir.exists(); //checks via temp File if the file to load exists
        if(exists) {
            ObjectInputStream in = null; //new ObjectInputStream
            try {
                in = new ObjectInputStream(new FileInputStream(fileName));
            } catch (FileNotFoundException fileNotFoundException ) {
                fileNotFoundException.printStackTrace(); //prints our the exception - only in dev mode
            }
            try {
                assert in != null;
                object = in.readObject();
            } catch (ClassNotFoundException | FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace(); //prints our the exception - only in dev mode
            }
            try {
                    in.close();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace(); //prints our the exception - only in dev mode
            }

        } else{
            throw new FileNotFoundException("No File"); //throwing No File exception
        }
        return(object);
    }

    private boolean isNotFloatCheck(String str){ //checks if the giving String is convertible to a float without exceptions
	      try {
	          Float.parseFloat(str);
	          return false;
	      } catch(NumberFormatException e){
	          return true;
          }
    }

    private void markField(JTextField textField,JLabel label, String labelText, Color color) { //procedure for marking input correction
        if (userSettings.getEmphasizeInputErrors()){ //checking the user settings
             if (userSettings.getHighlightInputErrors()) {
                 textField.setBorder(new LineBorder(color, 2));
             }
             if (userSettings.getHighlightDescribeInputErrors()) {
                  label.setForeground(color);
             }
             if (userSettings.getDescribeInputErrors()){
                 label.setText(labelText);
                 label.setVisible(true);
             }
        }
    }
    private void markFieldError(JTextField textField,JLabel label, String labelText) { //preset for marking input correction
        markField(textField,label, labelText, new Color(240,0,0)); //Color.red

    }
    private void markFieldCorrect(JTextField textField,JLabel label, String labelText) { //preset for marking input correction
        markField(textField,label, labelText, new Color(0,153,0));

    }
    private void markFieldOrange(JTextField textField, JLabel label, String labelText) { //preset for marking input correction
        markField(textField, label ,labelText, new Color(255, 150, 0));

    }

    private void resetMarkings(){ //resets the text field border colors and makes the error labels invisible
        textFieldOldKmReading.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border")); //resets Borders via the default LookAndFeel
        textFieldCurrentKmReading.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        textFieldTankedLiter.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

        labelErrorMsgOldKmReading.setForeground(Color.black);
        labelErrorMsgCurrentKmReading.setForeground(Color.black);
        labelErrorMsgTankedLiter.setForeground(Color.black);

        labelErrorMsgOldKmReading.setVisible(false);
        labelErrorMsgCurrentKmReading.setVisible(false);
        labelErrorMsgTankedLiter.setVisible(false);
    }

    private void convertTextFieldCommaToPoint(JTextField textField){
        String text = textField.getText();
        if (text.contains(",")){
            text = text.replaceAll(",", "."); //replace all "," with "."
            textField.setText(text);
        }
    }
//    Better Descriptions aka. "must be higher than 0"
    private boolean checkIsInputValid(){ //checks for Valid input and handles error highlighting

        convertTextFieldCommaToPoint(textFieldOldKmReading);
        convertTextFieldCommaToPoint(textFieldCurrentKmReading);
        convertTextFieldCommaToPoint(textFieldTankedLiter);

        String oKilo = textFieldOldKmReading.getText();
        String nKilo = textFieldCurrentKmReading.getText();
        String liter = textFieldTankedLiter.getText();

        String correctMsg = i18n.getString("correctMsg"); // \u2714 = ✔  generic msg for correct float
        String errorMsg = i18n.getString("errorMsg"); // \u274C = ❌  generic error for invalid float
        String errorMsgGreaterZero = i18n.getString("errorMsgGreaterZero"); // \u274C = ❌
        String errorMsgGreaterEqualZero = i18n.getString("errorMsgGreaterEqualZero"); // \u274C = ❌
        String errorMsgGreaterThan = MessageFormat.format(i18n.getString("errorMsgGreaterThan"), oKilo, labelOldKmReading.getText().substring(0, labelOldKmReading.getText().length() - 2) ); //.substring to erase the ":" Char
        String errorMsgResult = i18n.getString("errorMsgResult");
        String errorMsgResultDefault  =  MessageFormat.format(i18n.getString("labelResultDefault"), "");


        resetMarkings(); //resets the text field border colors and makes the error labels invisible

        boolean abort = false; //mark abort first as false -> input has no errors until somewhere specified

        if(isNotFloatCheck(oKilo)){ //check for string to float error
            markFieldError(textFieldOldKmReading, labelErrorMsgOldKmReading,errorMsg); //mark input error
            abort = true; //mark abort -> will later return invalid input, will later set error in result field
        } else {
            if( Float.parseFloat(oKilo) < 0){ //prevent negative value
                markFieldOrange(textFieldOldKmReading, labelErrorMsgOldKmReading, errorMsgGreaterEqualZero);
                abort = true;
            } else{
            markFieldCorrect(textFieldOldKmReading, labelErrorMsgOldKmReading,correctMsg); //mark field as correct
            }
        }
        if(isNotFloatCheck(nKilo)){
            markFieldError(textFieldCurrentKmReading, labelErrorMsgCurrentKmReading, errorMsg);
            abort = true;
        } else {
            if( Float.parseFloat(nKilo) <= 0){ //prevent negative and 0 value
                markFieldOrange(textFieldCurrentKmReading, labelErrorMsgCurrentKmReading, errorMsgGreaterZero);
                abort = true;
            } else{
            markFieldCorrect(textFieldCurrentKmReading, labelErrorMsgCurrentKmReading, correctMsg);
            }
        }
        if(isNotFloatCheck(liter)){
            markFieldError(textFieldTankedLiter, labelErrorMsgTankedLiter, errorMsg);
            abort = true;
        } else {
            if( Float.parseFloat(liter) <= 0){ //prevent negative and 0 value
                markFieldOrange(textFieldTankedLiter, labelErrorMsgTankedLiter, errorMsgGreaterZero);
                abort = true;
            } else{
            markFieldCorrect(textFieldTankedLiter, labelErrorMsgTankedLiter, correctMsg);
            }
        }
        if( !isNotFloatCheck(oKilo) && !isNotFloatCheck(nKilo) ){
            if( ( Float.parseFloat(nKilo) - Float.parseFloat(oKilo) ) <= 0 && Float.parseFloat(nKilo) > 0){ //check for division with negative number or null
            markFieldOrange(textFieldCurrentKmReading, labelErrorMsgCurrentKmReading, errorMsgGreaterThan); // \u274C = ❌
            abort = true;
        }}
        if(abort){ //decides if input errors are present
            labelResult.setText(errorMsgResult); //error message on result label
            return false; //input error
        } else {
            labelResult.setText(errorMsgResultDefault); //error message on result label
            return true; //no input error
        }
    }

    private void calcConsumption(){
        float result;
        Refuel refuel;

        if(checkIsInputValid()) {
            refuel = new Refuel(Float.parseFloat(textFieldOldKmReading.getText()), Float.parseFloat(textFieldCurrentKmReading.getText()), Float.parseFloat(textFieldTankedLiter.getText())); //new refuel object

            result = refuel.getConsumption(); //calling method to calculate the consumption

            labelResult.setText(MessageFormat.format(i18n.getString("resultMsg"), result)); //set result label text

            if(!refuelHistory.isEmpty()){ //prevent adding duplicate Refuel objects to refuelHistory after each other (duplicate Refuel Objects are still possible if they are not after each other)
                if( refuel.compareTo(refuelHistory.get( refuelHistory.size() - 1) ) == 0 ){ //comparing the last Refuel object of the refuelHistory with the new one
                    return; //stop at his point to prevent adding the new Refuel object to refuelHistory
                }
            }
            refuelHistory.add(refuel); //add refuel object to refuelHistory arrayList
        }

    }

    private void addListeners(){
        textFieldOldKmReading.addActionListener(e -> textFieldCurrentKmReading.requestFocusInWindow()); //sets Focus to next text field when pressing enter

        textFieldCurrentKmReading.addActionListener(e -> textFieldTankedLiter.requestFocusInWindow());

        textFieldTankedLiter.addActionListener(e -> buttonCalculate.requestFocusInWindow()); //sets Focus to calculate button when pressing enter

        DocumentListener checkInputOnDocumentChange = new DocumentListener() { //Listener running on Text Change
            final Runnable checkIsInputValid = () -> checkIsInputValid(); //Needed for the invokeLater

            @Override
            public void insertUpdate(DocumentEvent e) {
                if(userSettings.getDoAutoInputCheck()) { //checking userSettings
                    SwingUtilities.invokeLater(checkIsInputValid); //Needed because of the ReadWriteLock on the TextField, important for stopping exception with the convertTextFieldCommaToPoint Method
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(userSettings.getDoAutoInputCheck()) {
                    SwingUtilities.invokeLater(checkIsInputValid);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if(userSettings.getDoAutoInputCheck()) {
                    SwingUtilities.invokeLater(checkIsInputValid);
                }
            }
        };

        textFieldOldKmReading.getDocument().addDocumentListener(checkInputOnDocumentChange); //adding DocumentListener
        textFieldCurrentKmReading.getDocument().addDocumentListener(checkInputOnDocumentChange);
        textFieldTankedLiter.getDocument().addDocumentListener(checkInputOnDocumentChange);

        buttonCalculate.addActionListener(e -> //starts Consumption calculation by calling the respective method
                calcConsumption());

        aboutMenuItem.addActionListener(e -> //
                JOptionPane.showMessageDialog(GuiCalculator.this, i18n.getString("copyrightMsg"), i18n.getString("about"),  JOptionPane.PLAIN_MESSAGE)); //show About Dialog

        exitMenuItem.addActionListener(e ->
                System.exit(0)); //code "0" for program termination when everything went fine (non 0 = abnormal behaviour)

        tankLevelsMenuItem.addActionListener(e ->
                new GuiTankLevels(refuelHistory, userSettings)); //opens the Tank Levels Dialog

        resetTankLevelsMenuItem.addActionListener(e -> { //resets refuelHistory via creating a new empty refuelHistory

            int reset = JOptionPane.showOptionDialog(null, (MessageFormat.format(i18n.getString("ResetTankLevelsMsg"), GuiCalculator.this.refuelHistory.size())), (MessageFormat.format(i18n.getString("resetTankLevelsTitle"), GuiCalculator.this.refuelHistory.size())), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); //Reset Tank Level Dialog //returns 0 if yes was pressed
            if( reset == 0 ){ //0 if reset requested
                GuiCalculator.this.refuelHistory = new ArrayList<>(); //deletion of the arrayList by creating a new arrayList
            }
        });

        exportTankLevelsMenuItem.addActionListener(e -> { //exports TankLevels
            ArrayList<Refuel> refuelHistory = GuiCalculator.this.refuelHistory;
            try {
                saveObject(fileNameTankLevels, refuelHistory);
                JOptionPane.showMessageDialog(null, i18n.getString("exportTankLevelsSuccessMsg"), i18n.getString("exportTankLevelsSuccessTitle"), JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, i18n.getString("exportFailMsg"), i18n.getString("exportFailTitle"), JOptionPane.WARNING_MESSAGE); //conformation for failed export
                exception.printStackTrace(); //prints our the exception - only in dev mode
            }
        });

        importTankLevelsMenuItem.addActionListener(e -> { //imports TankLevels
            int doImport = JOptionPane.showOptionDialog(null, (MessageFormat.format(i18n.getString("importTankLevelsMsg"), GuiCalculator.this.refuelHistory.size())), (MessageFormat.format(i18n.getString("importTankLevelsTitle"), GuiCalculator.this.refuelHistory.size())), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); //import Tank Level Dialog //returns 0 if yes was pressed
            if( doImport == 0 ){ //0 if import requested
                ArrayList<Refuel> refuelHistory = new ArrayList<>(); //new refuelHistory
                try {
                    GuiCalculator.this.refuelHistory = (ArrayList<Refuel>) loadObject(fileNameTankLevels, refuelHistory); //loads the refuelHistory via the custom loadObject Method
                    JOptionPane.showMessageDialog(null, i18n.getString("importSuccessMsg"), i18n.getString("importSuccessTitle"), JOptionPane.INFORMATION_MESSAGE); //conformation for successful import
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, i18n.getString("importFailMsg"), i18n.getString("importFailTitle"), JOptionPane.WARNING_MESSAGE); //conformation for failed import
//                    System.out.println(exception);
                    exception.printStackTrace(); //prints our the exception - only in dev mode
                }
            }
        });

        changeSettingsItem.addActionListener(e -> { //Settings Dialog with live update (Custom Dialog)

            JLabel message = new JLabel(i18n.getString("settingsSelectPreferencesMsg"));
            message.setBorder(new EmptyBorder(0,0,10,0)); //small visual only border for spacing between Label and following component

            JCheckBox emphasizeInputErrorsCheckBox = new JCheckBox(i18n.getString("settingsEmphasizeInputErrors"), GuiCalculator.this.userSettings.getEmphasizeInputErrors()); //new Checkbox with Text and checked status read from user settings
            emphasizeInputErrorsCheckBox.addActionListener(e1 -> {
                GuiCalculator.this.userSettings.setEmphasizeInputErrors(emphasizeInputErrorsCheckBox.isSelected()); //sets user settings to Checkbox settings
                GuiCalculator.this.checkIsInputValid(); //recheck the Text Fields for live demonstration
            });

            JCheckBox highlightInputErrorsCheckBox = new JCheckBox(i18n.getString("settingsHighlightInputErrors"), GuiCalculator.this.userSettings.getHighlightInputErrors());
            highlightInputErrorsCheckBox.addActionListener(e2 -> {
                GuiCalculator.this.userSettings.setHighlightInputErrors(highlightInputErrorsCheckBox.isSelected());
                GuiCalculator.this.checkIsInputValid();
            });
            JCheckBox describeInputErrorsCheckBox = new JCheckBox(i18n.getString("settingsDescribeInputErrors"), GuiCalculator.this.userSettings.getDescribeInputErrors());
            describeInputErrorsCheckBox.addActionListener(e3 -> {
                GuiCalculator.this.userSettings.setDescribeInputErrors(describeInputErrorsCheckBox.isSelected());
                GuiCalculator.this.checkIsInputValid();
            });
            JCheckBox highlightDescribeInputErrorsCheckBox = new JCheckBox(i18n.getString("settingsHighlightDescribeInputErrors"), GuiCalculator.this.userSettings.getHighlightDescribeInputErrors());
            highlightDescribeInputErrorsCheckBox.addActionListener(e4 -> {
                GuiCalculator.this.userSettings.setHighlightDescribeInputErrors(highlightDescribeInputErrorsCheckBox.isSelected());
                GuiCalculator.this.checkIsInputValid();
            });
            JCheckBox doAutoInputCheck = new JCheckBox(i18n.getString("settingsDoAutoInputCheck"), GuiCalculator.this.userSettings.getDoAutoInputCheck());
            doAutoInputCheck.addActionListener(e4 -> {
                GuiCalculator.this.userSettings.setDoAutoInputCheck(doAutoInputCheck.isSelected());
                GuiCalculator.this.checkIsInputValid();
            });


            String[] comboBoxItems = {i18n.getString("languageDefault"),i18n.getString("languageEnglish"), i18n.getString("languageGerman")}; //Language change Combo Box -experimental
            JComboBox selectLanguage = new JComboBox(comboBoxItems);
            selectLanguage.addActionListener(e5 -> {
                if(selectLanguage.getSelectedIndex() == 1) {
                    GuiCalculator.this.userSettings.setProgramLocale(Locale.ENGLISH);
                    GuiCalculator.this.userSettings.setUseSystemLocale(false);
                    configureLanguage();
                    updateTextOfComponents();
                    checkIsInputValid();

                } else if (selectLanguage.getSelectedIndex() == 2) {
                    GuiCalculator.this.userSettings.setProgramLocale(Locale.GERMAN);
                    GuiCalculator.this.userSettings.setUseSystemLocale(false);
                    configureLanguage();
                    updateTextOfComponents();
                    checkIsInputValid();
                } else {
                    GuiCalculator.this.userSettings.setProgramLocale(defaultLocale);
                    GuiCalculator.this.userSettings.setUseSystemLocale(true);
                    configureLanguage();
                    updateTextOfComponents();
                    checkIsInputValid();
                }
            });

            if (GuiCalculator.this.userSettings.getUseSystemLocale()) {
                 selectLanguage.setSelectedIndex(0);
            } else if (GuiCalculator.this.userSettings.getProgramLocale().getLanguage().equals(Locale.ENGLISH.getLanguage())){
                selectLanguage.setSelectedIndex(1);
            } else if (GuiCalculator.this.userSettings.getProgramLocale().getLanguage().equals(Locale.GERMAN.getLanguage())){
                selectLanguage.setSelectedIndex(2);
            } else {
                 selectLanguage.setSelectedIndex(0);
                 GuiCalculator.this.userSettings.setProgramLocale(defaultLocale);
                 GuiCalculator.this.userSettings.setUseSystemLocale(true);
            }


            JPanel categoryHighlighting = new JPanel(); //Option subpart
            categoryHighlighting.setLayout(new BoxLayout(categoryHighlighting, BoxLayout.PAGE_AXIS));
            categoryHighlighting.setBorder(BorderFactory.createTitledBorder(i18n.getString("settingsCategoryHighlighting"))); //Visual border for context
            categoryHighlighting.add(emphasizeInputErrorsCheckBox);
            categoryHighlighting.add(highlightInputErrorsCheckBox);
            categoryHighlighting.add(describeInputErrorsCheckBox);
            categoryHighlighting.add(highlightDescribeInputErrorsCheckBox);
            categoryHighlighting.setAlignmentX(Component.LEFT_ALIGNMENT); //Align Left

            JPanel categoryAutomation = new JPanel(); //Option subpart
            categoryAutomation.setLayout(new BoxLayout(categoryAutomation, BoxLayout.PAGE_AXIS));
            categoryAutomation.setBorder(BorderFactory.createTitledBorder(i18n.getString("settingsCategoryAutomation")));
            categoryAutomation.add(doAutoInputCheck);
            categoryAutomation.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel categoryLanguage = new JPanel(); //Option subpart
            categoryLanguage.setLayout(new BoxLayout(categoryLanguage, BoxLayout.PAGE_AXIS));
            categoryLanguage.setBorder(BorderFactory.createTitledBorder(i18n.getString("language")));
            categoryLanguage.add(selectLanguage);
            categoryLanguage.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel dialogContent = new JPanel(); //Option Panel
            dialogContent.setLayout(new BoxLayout(dialogContent, BoxLayout.PAGE_AXIS));
            dialogContent.add(message);
            dialogContent.add(categoryHighlighting);
            dialogContent.add(categoryAutomation);
            dialogContent.add(categoryLanguage);

            Object[] options = {i18n.getString("settingsOptionOkRuntime"), i18n.getString("settingsOptionOkFile")}; //Options for Confirming the Dialog
            int save = JOptionPane.showOptionDialog( null , dialogContent, i18n.getString("settingsTitle"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[0]); //(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue)
            if( save == 1 ) { //detects second Button press on Dialog
                saveUserSettings( GuiCalculator.this.userSettings); //saves User Settings to File
            }

        });
    }

     private void initComponents(){

        //labels
        labelOldKmReading = new JLabel();
        labelCurrentKmReading = new JLabel();
        labelTankedLiter = new JLabel();
        labelResult = new JLabel();
        labelErrorMsgOldKmReading = new JLabel();
        labelErrorMsgCurrentKmReading = new JLabel();
        labelErrorMsgTankedLiter = new JLabel();

        //text fields
        textFieldOldKmReading = new JTextField();
        textFieldCurrentKmReading = new JTextField();
        textFieldTankedLiter = new JTextField();

        //buttons
        buttonCalculate = new JButton();
        getRootPane().setDefaultButton(buttonCalculate); //makes the calculate button the default button of the window

        //menu and respective Accelerators and Mnemonics, indentation is representative for final Structure
        mainMenuBar = new JMenuBar();
            fileMenu = new JMenu();
            fileMenu.setMnemonic(KeyEvent.VK_F);
                tankLevelsMenuItem = new JMenuItem();
                tankLevelsMenuItem.setMnemonic(KeyEvent.VK_T);
                tankLevelsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK));

                exitMenuItem = new JMenuItem();
                exitMenuItem.setMnemonic(KeyEvent.VK_X);
                exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));

            editMenu = new JMenu();
            editMenu.setMnemonic(KeyEvent.VK_E);
                resetTankLevelsMenuItem = new JMenuItem();
                resetTankLevelsMenuItem.setMnemonic(KeyEvent.VK_R);
                resetTankLevelsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));

                exportTankLevelsMenuItem = new JMenuItem();
                exportTankLevelsMenuItem.setMnemonic(KeyEvent.VK_O);
                exportTankLevelsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK));

                importTankLevelsMenuItem = new JMenuItem();
                importTankLevelsMenuItem.setMnemonic(KeyEvent.VK_I);
                importTankLevelsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));

            helpMenu = new JMenu();
            helpMenu.setMnemonic(KeyEvent.VK_H);
                aboutMenuItem = new JMenuItem();
                aboutMenuItem.setMnemonic(KeyEvent.VK_A);
                aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));

                changeSettingsItem = new JMenuItem();
                changeSettingsItem.setMnemonic(KeyEvent.VK_S);
                changeSettingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));

        //vars
        refuelHistory = new ArrayList<>();
    }

    private void updateTextOfComponents(){
        //Application
        setTitle(i18n.getString("appTitle"));

        //Labels
        labelOldKmReading.setText(MessageFormat.format(i18n.getString("labelOldKmReading"), "" ));
        labelCurrentKmReading.setText(MessageFormat.format(i18n.getString("labelCurrentKmReading"), "" ));
        labelTankedLiter.setText(MessageFormat.format(i18n.getString("labelTankedLiter"), "" ));
        labelResult.setText(MessageFormat.format(i18n.getString("labelResultDefault"), "" ));

        //buttons
        buttonCalculate.setText(i18n.getString("buttonCalculate"));

        //menus
        fileMenu.setText(i18n.getString("fileMenu"));
            tankLevelsMenuItem.setText(i18n.getString("tankLevelsMenuItem"));
            exitMenuItem.setText(i18n.getString("exitMenuItem"));
        editMenu.setText(i18n.getString("editMenu"));
            resetTankLevelsMenuItem.setText(i18n.getString("resetTankLevelsMenuItem"));
            exportTankLevelsMenuItem.setText(i18n.getString("exportTankLevelsMenuItem"));
            importTankLevelsMenuItem.setText(i18n.getString("importTankLevelsMenuItem"));
        helpMenu.setText(i18n.getString("helpMenu"));
            aboutMenuItem.setText(i18n.getString("aboutMenuItem"));
            changeSettingsItem.setText(i18n.getString("changeSettingsItem"));
    }

    private void initUi(){
        //Menu bar, indentation is representative for final Structure
        setJMenuBar(mainMenuBar);
            mainMenuBar.add(fileMenu);
                fileMenu.add(tankLevelsMenuItem);
                fileMenu.add(exitMenuItem);
            mainMenuBar.add(editMenu);
                editMenu.add(resetTankLevelsMenuItem);
                editMenu.add(exportTankLevelsMenuItem);
                editMenu.add(importTankLevelsMenuItem);
            mainMenuBar.add(helpMenu);
                helpMenu.add(aboutMenuItem);
                helpMenu.add(changeSettingsItem);

        //for all components
        GridBagConstraints c = new GridBagConstraints(); //constraints object for object positions
//        c.weightx = 1; //weight distribution of all UI objects  //If not set -> less UI movement when typing //try to enable/disable
        c.weighty = 1; //weight distribution of all UI objects
        c.fill = GridBagConstraints.HORIZONTAL; //stretch horizontal for all UI objects

        //for left label components
        c.gridx = 0;
        c.insets = new Insets( 0,10,0,10);

        c.gridy = 0;
        add(labelOldKmReading, c);

        c.gridy = 2;
        add(labelCurrentKmReading, c);

        c.gridy = 4;
        add(labelTankedLiter, c);

        //for right text field components
        c.weightx = 2;
        c.gridx = 1;
        c.insets = new Insets( 0,0,0,10);

        c.gridy = 0;
        add(textFieldOldKmReading, c);

        c.gridy = 1;
        add(labelErrorMsgOldKmReading, c);

        c.gridy = 2;
        add(textFieldCurrentKmReading, c);

        c.gridy = 3;
        add(labelErrorMsgCurrentKmReading, c);

        c.gridy = 4;
        add(textFieldTankedLiter, c);

        c.gridy = 5;
        add(labelErrorMsgTankedLiter, c);

        //for bottom Centered components
        c.insets = new Insets( 0,10,0,10);
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.CENTER;

        c.gridy = 6;
        add(buttonCalculate, c);

        c.gridy = 7;
        add(labelResult, c);
    }
}
