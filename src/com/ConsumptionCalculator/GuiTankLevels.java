package com.ConsumptionCalculator;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class GuiTankLevels extends JDialog { //Dialog

    //Labels
    private JLabel labelOldKmReading;
    private JLabel labelCurrentKmReading;
    private JLabel labelTankedLiter;
    private JLabel labelResult;
    private JLabel labelCurrentPage;


    //Buttons
    private JButton buttonNextItem;
    private JButton buttonPrevItem;
    private JButton buttonClose;

    //vars
    private final ArrayList<Refuel> refuelHistory;
    private int refuelHistoryPosition;

    private static ResourceBundle i18n;

    public GuiTankLevels(ArrayList<Refuel> refuelHistory, UserSettings userSettings){

        Locale.setDefault(userSettings.getProgramLocale());
        ResourceBundle.clearCache();
        i18n = ResourceBundle.getBundle("com.ConsumptionCalculator.i18n");

        setLayout(new GridBagLayout()); //set UI Layout to grid bag layout
        setModal(true); //makes this Dialog modal

        this.setTitle(MessageFormat.format(i18n.getString("tankLevelsDialogTitle"), 0, 0));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("calculator512.png"))); //set Icon

        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Not needed here

        setSize(400,250);
        setResizable(true);

        initComponents(); //set definition and initial settings of the components

        this.refuelHistory = refuelHistory; //setting vars from constructor (needs to be here)
        this.refuelHistoryPosition = refuelHistory.size() - 1; //setting vars from constructor (needs to be here)

        refreshDisplay(); //refreshes all Labels and Window Title with current Text
        addListeners(); //add listeners to objects
        initUi(); //set the location and nesting of UI objects


        setLocationRelativeTo(null); //app location in the middle of the screen
        setVisible(true);
    }

    private void nextItem(){
        if( refuelHistoryPosition < ( refuelHistory.size() - 1 ) ){ //go to next item
                  refuelHistoryPosition++;
              }
        else if( refuelHistoryPosition == ( refuelHistory.size() - 1 ) ){ //Wrap to first entry
            refuelHistoryPosition = 0;
        }
        refreshDisplay();
    }

    private void prevItem(){
        if( refuelHistoryPosition > 0 ){ //go to previous item
                  refuelHistoryPosition--;
              }
        else if( this.refuelHistoryPosition == 0 ){ //Wrap to last entry
            refuelHistoryPosition = ( refuelHistory.size() - 1 );
        }
        refreshDisplay();
    }

    private void refreshDisplay(){
        if( refuelHistory.size() >= 1){ //action when RefuelHistory is not empty
            labelOldKmReading.setText( MessageFormat.format( i18n.getString("labelOldKmReading"), refuelHistory.get(refuelHistoryPosition).getOldKilometerReading() ) );
            labelCurrentKmReading.setText( MessageFormat.format( i18n.getString("labelCurrentKmReading"), refuelHistory.get(refuelHistoryPosition).getNewKilometerReading() ) );
            labelTankedLiter.setText( MessageFormat.format( i18n.getString("labelTankedLiter"), refuelHistory.get(refuelHistoryPosition).getLiter() ) );
            labelResult.setText( MessageFormat.format( i18n.getString("resultMsg"), refuelHistory.get(refuelHistoryPosition).getConsumption() ) );
            labelCurrentPage.setText(MessageFormat.format(i18n.getString("tankLevelsDialogLabelCurrentPage"), refuelHistoryPosition + 1, refuelHistory.size()));
            this.setTitle( MessageFormat.format(i18n.getString("tankLevelsDialogTitle"), refuelHistoryPosition + 1, refuelHistory.size()) );
        }
        else{ //action when RefuelHistory is empty
            labelCurrentPage.setText(MessageFormat.format(i18n.getString("tankLevelsDialogLabelCurrentPage"), 0, 0)); //separated for possible translation
            this.setTitle(MessageFormat.format(i18n.getString("tankLevelsDialogTitle"), 0, 0));
        }
    }

    private void initComponents(){

        //labels
        labelOldKmReading = new JLabel(MessageFormat.format(i18n.getString("labelOldKmReading"), "" ));
        labelCurrentKmReading = new JLabel(MessageFormat.format(i18n.getString("labelCurrentKmReading"), "" ));
        labelTankedLiter = new JLabel(MessageFormat.format(i18n.getString("labelTankedLiter"), "" ));
        labelResult = new JLabel(MessageFormat.format(i18n.getString("labelResultDefault"), "" ));
        labelCurrentPage = new JLabel(MessageFormat.format(i18n.getString("tankLevelsDialogLabelCurrentPage"), 0, 0));


        //buttons
        buttonPrevItem = new JButton(i18n.getString("tankLevelsDialogButtonPrevItem"));
        buttonNextItem = new JButton(i18n.getString("tankLevelsDialogButtonNextItem"));
        buttonClose = new JButton(i18n.getString("tankLevelsDialogButtonClose"));
    }

    private void addListeners(){
        buttonNextItem.addActionListener(e -> nextItem());

        buttonPrevItem.addActionListener(e -> prevItem());

        buttonClose.addActionListener(e -> dispose());
    }

    private void initUi(){

        //for all components
        GridBagConstraints c = new GridBagConstraints(); //constraints object for object positions
        c.weightx = 1; //weight distribution of all UI objects
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL; //stretch horizontal for all UI objects

        //for label components
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets( 0,10,0,10);

        c.gridy = 0;
        add(labelOldKmReading, c);

        c.gridy = 1;
        add(labelCurrentKmReading, c);

        c.gridy = 2;
        add(labelTankedLiter, c);

        c.gridy = 3;
        add(labelResult, c);

        //for buttons and current Page label
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.CENTER;

        c.gridx = 0;
        add(buttonPrevItem, c);

        c.gridx = 1;
        add(buttonNextItem, c);

        c.gridy = 5;
        c.gridx = 0;
        add(buttonClose, c);

        c.gridy = 5;
        c.gridx = 1;
        add(labelCurrentPage, c);
    }
}
