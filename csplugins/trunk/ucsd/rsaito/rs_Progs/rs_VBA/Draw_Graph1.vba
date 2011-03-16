Option Explicit


Sub Draw_Graph()

    Draw_graph1 (ActiveCell.Row)

End Sub


Sub Draw_Graph_Auto()

    Dim row_pointer As Long
    Dim Sheet_name As String
    Sheet_name = ActiveSheet.Name
    
    row_pointer = 2
    Do While Cells(row_pointer, 1).Value <> ""
        If Cells(row_pointer, 3).Value <> "" Then
            Draw_graph1 (row_pointer)
            Sheets(Sheet_name).Select
        End If
        row_pointer = row_pointer + 1
    Loop

End Sub

Function Draw_graph1(ByVal active_row As Long)
    
    Dim Range_strS As String
    
    Dim Data_name As String, Graph_name As String, Data_num As String
    Dim Sheet_name As String
    
    Const x_labels = "R1C4:R1C9"
    Const x_from = "D"
    Const x_to = "I"
    
    Const x_flag_from = 14
    Const x_flag_to = 16
    
    Dim flag_a(x_flag_to - x_flag_from + 1) As String
    Dim flag As String
    Dim first_f As Boolean
    
    Dim i As Integer
    
    Sheet_name = ActiveSheet.Name
    
    Data_num = Cells(active_row, 1)
    Data_name = Cells(active_row, 2)

    Range_strS = x_from & LTrim(Str(active_row)) & ":" & x_to & LTrim(Str(active_row))

    For i = x_flag_from To x_flag_to
        If Cells(active_row, i) <> "" Then
            flag_a(i - x_flag_from) = Cells(1, i)
        Else
            flag_a(i - x_flag_from) = ""
        End If
    Next i
    
    first_f = True
    flag = ""
    
    For i = x_flag_from To x_flag_to
        If flag_a(i - x_flag_from) <> "" Then
            If first_f = False Then
                flag = flag & ","
            End If
            flag = flag & flag_a(i - x_flag_from)
            first_f = False
        End If
    Next i

    Range(Range_strS).Select
    Charts.Add
    ActiveChart.ChartType = xlLineMarkers
    ActiveChart.SetSourceData Source:=Sheets(Sheet_name).Range(Range_strS), PlotBy:= _
        xlRows
    ActiveChart.SeriesCollection(1).Name = Data_name
    ActiveChart.SeriesCollection(1).XValues = "=" & Sheet_name & "!" & x_labels
    ActiveChart.Location Where:=xlLocationAsNewSheet, Name:=Data_num & "-" & Data_name
    With ActiveChart
        .HasTitle = True
        .ChartTitle.Characters.Text = Data_name & " [" & flag & "]"
        .Axes(xlCategory, xlPrimary).HasTitle = True
        .Axes(xlCategory, xlPrimary).AxisTitle.Characters.Text = "Time Point"
        .Axes(xlValue, xlPrimary).HasTitle = True
        .Axes(xlValue, xlPrimary).AxisTitle.Characters.Text = "Intensity"
        .Legend.Delete
    End With

End Function
