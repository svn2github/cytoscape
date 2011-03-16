
Sub predlinkauto()
'
'

    Dim rowp As Integer
    Dim LinkColumn As Integer
    Dim FlagColumn As Integer
    Dim filepath As String
    
    
    LinkColumn = 31
    FlagColumn = 3 'Always OK
    
    rowp = 2
    
    Do While Cells(rowp, 1) <> "" And Cells(rowp, 2) <> ""

        If Cells(rowp, FlagColumn) <> "" Then
            Cells(rowp, LinkColumn).Select
            filepath = "PredLog_Win\" & Cells(rowp, 1) & "\" & Cells(rowp, 1) & "-" & Cells(rowp, 2) & ".txt"
            ActiveSheet.Hyperlinks.Add Anchor:=Selection, Address:= _
                filepath, TextToDisplay:="Prediction Detail"
        End If
        
        rowp = rowp + 1
    Loop
    
End Sub
