package cern.enice.jira.amh.jiracommunicator;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.search.ReceivedDateTerm;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import cern.enice.jira.amh.api.NetworkClient;
import cern.enice.jira.amh.dto.HttpRequest;
import cern.enice.jira.amh.dto.HttpResponse;
import cern.enice.jira.amh.dto.IssueDescriptor;
import cern.enice.jira.amh.dto.Result;
import cern.enice.jira.amh.logback.LogbackLogProvider;
import cern.enice.jira.amh.network_client.NetworkClientJerseyImpl;
import cern.enice.jira.amh.utils.ResultCode;
import cern.enice.jira.amh.utils.Utils;

@RunWith(JUnitParamsRunner.class)
public class CommentIssueTest extends JiraRestCommunicatorBaseTest {

	@Parameters
	@Test
	public void testCommentIssue(String issueKey, String comment, String author, String visibility, String attachments, HttpResponse response, Result expectedResult) {
		issueDescriptor.setKey(issueKey);
		issueDescriptor.setCommentAuthor(author);
		issueDescriptor.setCommentVisibleTo(visibility);
		issueDescriptor.setComment(comment);
		if (attachments != null)
			issueDescriptor.setAttachments(attachments.split(";"));
		NetworkClient networkClient = jiraCommunicator.getNetworkClient();
		doReturn(response).when(networkClient).request((HttpRequest)anyObject());
		Result result = jiraCommunicator.commentIssue(issueDescriptor);
		assertThat(result.getCode(), is(expectedResult.getCode()));
	}
	
	private Object parametersForTestCommentIssue() {
		return $(
				$(null, null, null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("", null, null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$(" ", null, null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", null, null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "", null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", " ", null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", null, null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "", null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", " ", null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", null, null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", "", null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", " ", null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", "users", null, null, new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", "users", null, new HttpResponse(400, null), new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", "users", null, new HttpResponse(100, null), new Result(ResultCode.COMMENT_NOT_ADDED)),
				$("PROJECT-123", "Test comment", "username", "users", null, new HttpResponse(200, null), new Result(ResultCode.COMMENT_ADDED)),
				$("PROJECT-123", "Test comment", "username", null, null, new HttpResponse(200, null), new Result(ResultCode.COMMENT_ADDED)),
				$("PROJECT-123", "Test comment", "username", null, "", new HttpResponse(200, null), new Result(ResultCode.COMMENT_ADDED)),
				$("PROJECT-123", "Test comment", "username", null, " ", new HttpResponse(200, null), new Result(ResultCode.COMMENT_ADDED)),
				$("PROJECT-123", "Test comment", "username", null, "attachment1", new HttpResponse(200, null), new Result(ResultCode.COMMENT_ADDED)),
				$("PROJECT-123", "Test comment", "username", null, "attachment1;attachment2", new HttpResponse(200, null), new Result(ResultCode.COMMENT_ADDED))
		);
	}
}
