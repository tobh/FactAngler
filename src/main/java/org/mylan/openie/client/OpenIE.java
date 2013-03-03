package org.mylan.openie.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import org.mylan.openie.shared.SimpleRelation;
import org.mylan.openie.shared.Result;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenIE implements EntryPoint {
    private Panel resultPanel = new VerticalPanel();
    private Label status = new Label();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final TextBox searchBox = new TextBox();
        searchBox.setStyleName("search_box");
        final Button searchButton = new Button("Search");
        searchButton.setStyleName("search_button");
        final FlowPanel searchPanel = new FlowPanel();
        searchPanel.add(searchBox);
        searchPanel.add(searchButton);
        searchPanel.setStyleName("search_panel");

        resultPanel.setStyleName("result_panel");
        status.setStyleName("status");

        searchBox.addKeyboardListener(new KeyboardListenerAdapter() {
                public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                    String query = ((TextBox) sender).getText();
                    if (keyCode == KEY_ENTER) {
                        search(query);
                    }
                }
            });

        searchButton.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    search(searchBox.getText());
                }
            });

        final VerticalPanel relationSearchPanel = new VerticalPanel();
        relationSearchPanel.add(searchPanel);
        relationSearchPanel.add(status);
        relationSearchPanel.add(resultPanel);

        RootPanel.get().add(relationSearchPanel);
    }

    private void search(final String searchTerm) {
        resultPanel.clear();
        status.setText("Searching ...");

        RelationSearchServiceAsync relationSearchService = (RelationSearchServiceAsync) GWT.create(RelationSearchService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) relationSearchService;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "relationSearch";
        endpoint.setServiceEntryPoint(moduleRelativeURL);

        AsyncCallback<Result<SimpleRelation>> callback = new AsyncCallback<Result<SimpleRelation>>() {
                public void onSuccess(Result<SimpleRelation> result) {
                    resultPanel.clear();
                    resultPanel.add(createResultTable(result));
                    status.setText("Found " + result.getResultCount() + " results");
                }

                public void onFailure(Throwable e) {
                    resultPanel.clear();
                    resultPanel.add(new HTML(e.toString()));
                }
            };
        relationSearchService.search(searchTerm, callback);
    }

    private Widget createResultTable(final Result<SimpleRelation> result) {
        FlexTable table = new FlexTable();
        for (SimpleRelation relation : result.getResults()) {
            int currentRow = table.getRowCount();
            table.insertRow(currentRow);
            createCell(table, currentRow, relation.getFirstArgument());
            createCell(table, currentRow, relation.getRelationPattern());
            createCell(table, currentRow, relation.getLastArgument());
            createCell(table, currentRow, "" + relation.getIsRelationProbability());
            createCell(table, currentRow, "" + relation.getNoRelationProbability());
            createCell(table, currentRow, relation.getSentence());
        }
        return table;
    }

    private void createCell(final FlexTable table, int currentRow, final String text) {
        int currentCell = table.getCellCount(currentRow);
        table.insertCell(currentRow, currentCell);
        table.setHTML(currentRow, currentCell, text);
    }
}
