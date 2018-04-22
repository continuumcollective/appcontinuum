package io.barinek.continuum.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.utils.BasicHandler
import io.barinek.continuum.dataaccess.TimeEntryDataGateway
import io.barinek.continuum.models.TimeEntryInfo
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TimeEntryController(val mapper: ObjectMapper, val gateway: TimeEntryDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/time-entries", request, httpServletResponse) {
            val entry = mapper.readValue(request.reader, TimeEntryInfo::class.java)
            val record = gateway.create(entry.projectId, entry.userId, entry.date, entry.hours)
            mapper.writeValue(httpServletResponse.outputStream, TimeEntryInfo(record.id, record.projectId, record.userId, record.date, record.hours, "entry info"))
        }
        get("/time-entries", request, httpServletResponse) {
            val userId = request.getParameter("userId")
            val list = gateway.findBy(userId.toLong()).map { record ->
                TimeEntryInfo(record.id, record.projectId, record.userId, record.date, record.hours, "entry info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }
}