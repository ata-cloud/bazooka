/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.base.git;

import com.youyu.common.exception.BizException;
import hudson.Util;
import net.atayun.bazooka.base.enums.code.OpsCommonExceptionCode;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author rache
 * @date 2019-08-29
 */
public class JGitHelpImpl implements GitServiceHelp {

    public Map<String, ObjectId> getRemoteReferences(String url, String userName, String pwd) {
        String pattern=null;
        boolean headsOnly=true;
        boolean tagsOnly=false;
        Map<String, ObjectId> references = new HashMap<>();
        String regexPattern = null;
        if (pattern != null) {
            regexPattern = createRefRegexFromGlob(pattern);
        }
        try (Repository repo = openDummyRepository()) {
            LsRemoteCommand lsRemote = new LsRemoteCommand(repo);
            if (headsOnly) {
                lsRemote.setHeads(headsOnly);
            }
            if (tagsOnly) {
                lsRemote.setTags(tagsOnly);
            }
            lsRemote.setRemote(url);
            UsernamePasswordCredentialsProvider provider =new UsernamePasswordCredentialsProvider(userName,pwd);
            lsRemote.setCredentialsProvider(provider);
            Collection<Ref> refs = lsRemote.call();
            for (final Ref r : refs) {
                final String refName = r.getName();
                final ObjectId refObjectId =
                        r.getPeeledObjectId() != null ? r.getPeeledObjectId() : r.getObjectId();
                if (regexPattern != null) {
                    if (refName.matches(regexPattern)) {
                        references.put(refName, refObjectId);
                    }
                } else {
                    references.put(refName, refObjectId);
                }
            }
        } catch (GitAPIException | IOException e) {
            throw new BizException(OpsCommonExceptionCode.Git_ERROR_BRANCH.getCode(),e.getMessage());
        }
        return references;
    }
    @Override
    public Set<String> branchList(String gitUrl,String userName,String pwd) {

            Set<String> branchSet = new HashSet<>();
            Map<String, ObjectId> branches = getRemoteReferences(gitUrl,userName,pwd);
            if(branches==null){
                throw new BizException(OpsCommonExceptionCode.Git_ERROR_BRANCH.getCode(),"git分支为空");
            }
            Iterator<String> remoteBranchesName = branches.keySet().iterator();
            while (remoteBranchesName.hasNext()) {
                String branchName = strip(remoteBranchesName.next());
                branchSet.add(branchName);
            }
            return branchSet;

    }
    private String strip(String name) {
        return name.substring(name.indexOf('/', 5) + 1);
    }

    private Repository openDummyRepository() throws IOException {
        final File tempDir = Util.createTempDir();
        return new FileRepository(tempDir) {
            @Override
            public void close() {
                super.close();
                try {
                    Util.deleteRecursive(tempDir);
                } catch (IOException e) {
                    // ignore
                }
            }
        };
    }

    private String createRefRegexFromGlob(String glob)
    {
        StringBuilder out = new StringBuilder();
        out.append('^');
        if(!glob.startsWith("refs/")) {
            out.append(".*/");
        }
        out.append(replaceGlobCharsWithRegExChars(glob));
        out.append('$');
        return out.toString();
    }

    private String replaceGlobCharsWithRegExChars(String glob)
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch(c) {
                case '*':
                    out.append(".*");
                    break;
                case '?':
                    out.append('.');
                    break;
                case '.':
                    out.append("\\.");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                default:
                    out.append(c);
                    break;
            }
        }
        return out.toString();
    }
}
